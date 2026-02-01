package com.usai.rag.service.impl;

import com.usai.rag.api.dto.IngestRequest;
import com.usai.rag.api.dto.IngestResponse;
import com.usai.rag.config.RagProperties;
import com.usai.rag.model.ChunkInput;
import com.usai.rag.model.DocumentInput;
import com.usai.rag.repository.IngestRepository;
import com.usai.rag.service.IngestService;
import com.usai.rag.service.ingest.Chunker;
import com.usai.rag.service.ingest.DocumentLoader;
import com.usai.rag.service.ingest.DocumentMetadataExtractor;
import com.usai.rag.service.metrics.RagMetrics;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IngestServiceImpl implements IngestService {
    private static final Logger log = LoggerFactory.getLogger(IngestServiceImpl.class);

    private final DocumentLoader documentLoader;
    private final Chunker chunker;
    private final DocumentMetadataExtractor metadataExtractor;
    private final EmbeddingModel embeddingModel;
    private final IngestRepository ingestRepository;
    private final RagProperties ragProperties;
    private final RagMetrics ragMetrics;
    private final Tracer tracer;

    public IngestServiceImpl(
        DocumentLoader documentLoader,
        Chunker chunker,
        DocumentMetadataExtractor metadataExtractor,
        EmbeddingModel embeddingModel,
        IngestRepository ingestRepository,
        RagProperties ragProperties,
        RagMetrics ragMetrics,
        Tracer tracer
    ) {
        this.documentLoader = documentLoader;
        this.chunker = chunker;
        this.metadataExtractor = metadataExtractor;
        this.embeddingModel = embeddingModel;
        this.ingestRepository = ingestRepository;
        this.ragProperties = ragProperties;
        this.ragMetrics = ragMetrics;
        this.tracer = tracer;
    }

    @Override
    public IngestResponse ingest(IngestRequest request, long latencyMs) {
        IngestResponse response = new IngestResponse();
        int documents = 0;
        int chunks = 0;
        int errors = 0;

        long start = System.nanoTime();
        Span ingestSpan = tracer.spanBuilder("ingest_run").startSpan();
        try {
            Path root = Path.of(request.getPath()).toAbsolutePath();
            Predicate<Path> include = buildMatcher(root, request.getInclude());
            Predicate<Path> exclude = buildMatcher(root, request.getExclude());
            List<DocumentInput> docs = documentLoader.load(root, include, exclude);

            for (DocumentInput doc : docs) {
                try {
                    Span parseSpan = tracer.spanBuilder("ingest_parse").startSpan();
                    try {
                        // content already parsed in loader
                    } finally {
                        parseSpan.end();
                    }

                    String checksum = sha256(doc.content());
                    Map<String, Object> meta = metadataExtractor.extract(doc.path());
                    String service = (String) meta.getOrDefault("service", null);
                    String environment = (String) meta.getOrDefault("environment", null);
                    List<String> tags = Collections.emptyList();

                    var documentId = ingestRepository.upsertDocument(doc, checksum, tags, service, environment);
                    ingestRepository.deleteChunksByDocument(documentId);

                    List<ChunkInput> docChunks = chunker.chunk(
                        doc.content(),
                        ragProperties.getChunk().getSizeTokens(),
                        ragProperties.getChunk().getOverlapTokens()
                    );

                    List<float[]> embeddings = new ArrayList<>(docChunks.size());
                    Span embedSpan = tracer.spanBuilder("ingest_embed").startSpan();
                    try {
                        for (ChunkInput chunk : docChunks) {
                            Embedding embedding = embeddingModel.embed(chunk.content()).content();
                            embeddings.add(embedding.vector());
                        }
                    } finally {
                        embedSpan.end();
                    }

                    Span dbSpan = tracer.spanBuilder("db_write").startSpan();
                    try {
                        ingestRepository.insertChunks(documentId, docChunks, embeddings);
                    } finally {
                        dbSpan.end();
                    }
                    documents += 1;
                    chunks += docChunks.size();
                } catch (Exception e) {
                    errors += 1;
                    log.error("Failed to ingest document {}", doc.path(), e);
                }
            }
        } catch (IOException e) {
            errors += 1;
            log.error("Ingest failed", e);
        }

        response.setStatus("ok");
        response.setDocuments(documents);
        response.setChunks(chunks);
        response.setErrors(errors);
        response.setLatencyMs(latencyMs);
        ragMetrics.recordIngest((System.nanoTime() - start) / 1_000_000L, documents, errors);
        ingestSpan.end();
        return response;
    }

    private Predicate<Path> buildMatcher(Path root, String[] patterns) {
        if (patterns == null || patterns.length == 0) {
            return null;
        }
        List<PathMatcher> matchers = new ArrayList<>();
        for (String pattern : patterns) {
            matchers.add(FileSystems.getDefault().getPathMatcher("glob:" + pattern));
        }
        return path -> {
            Path rel = root.relativize(path);
            for (PathMatcher matcher : matchers) {
                if (matcher.matches(rel)) {
                    return true;
                }
            }
            return false;
        };
    }

    private String sha256(String content) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}

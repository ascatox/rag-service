package com.usai.rag.service.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class RagMetrics {
    private final Timer ingestLatency;
    private final Counter ingestErrors;
    private final Counter ingestDocuments;

    private final Timer retrievalLatency;
    private final Counter retrievalHits;

    private final Timer llmLatency;
    private final Counter llmErrors;

    public RagMetrics(MeterRegistry registry) {
        this.ingestLatency = Timer.builder("rag_ingest_latency")
            .description("Ingest latency")
            .register(registry);
        this.ingestErrors = Counter.builder("rag_ingest_errors_total")
            .description("Ingest errors")
            .register(registry);
        this.ingestDocuments = Counter.builder("rag_ingest_documents_total")
            .description("Ingested documents")
            .register(registry);

        this.retrievalLatency = Timer.builder("rag_retrieval_latency")
            .description("Retrieval latency")
            .register(registry);
        this.retrievalHits = Counter.builder("rag_retrieval_hits_total")
            .description("Retrieved chunks count")
            .register(registry);

        this.llmLatency = Timer.builder("rag_llm_latency")
            .description("LLM latency")
            .register(registry);
        this.llmErrors = Counter.builder("rag_llm_errors_total")
            .description("LLM errors")
            .register(registry);
    }

    public void recordIngest(long durationMs, int documents, int errors) {
        ingestLatency.record(durationMs, TimeUnit.MILLISECONDS);
        ingestDocuments.increment(documents);
        if (errors > 0) {
            ingestErrors.increment(errors);
        }
    }

    public void recordRetrieval(long durationMs, int hits) {
        retrievalLatency.record(durationMs, TimeUnit.MILLISECONDS);
        retrievalHits.increment(hits);
    }

    public void recordLlm(long durationMs, boolean error) {
        llmLatency.record(durationMs, TimeUnit.MILLISECONDS);
        if (error) {
            llmErrors.increment();
        }
    }
}

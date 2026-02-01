package com.usai.rag.config;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class EmbeddingDimensionChecker implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(EmbeddingDimensionChecker.class);

    private static final Map<String, Integer> KNOWN_MODEL_DIMS = Map.of(
        "text-embedding-3-small", 1536,
        "text-embedding-3-large", 3072
    );

    private final OpenAiProperties openAiProperties;
    private final RagProperties ragProperties;

    public EmbeddingDimensionChecker(OpenAiProperties openAiProperties, RagProperties ragProperties) {
        this.openAiProperties = openAiProperties;
        this.ragProperties = ragProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        String model = openAiProperties.getModel().getEmbeddings();
        if (!StringUtils.hasText(model)) {
            return;
        }

        Integer expected = KNOWN_MODEL_DIMS.get(model);
        if (expected == null) {
            log.info("Embedding model '{}' not in known list; skipping dimension check.", model);
            return;
        }

        int configured = ragProperties.getEmbedding().getDimensions();
        if (configured != expected) {
            log.warn(
                "Embedding dimension mismatch: model '{}' expects {} but rag.embedding.dimensions is {}.",
                model,
                expected,
                configured
            );
        }
    }
}

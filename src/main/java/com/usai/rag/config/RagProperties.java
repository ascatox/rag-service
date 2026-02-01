package com.usai.rag.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "rag")
@Validated
public class RagProperties {
    private Embedding embedding = new Embedding();
    private Retrieval retrieval = new Retrieval();
    private Chunk chunk = new Chunk();

    public Embedding getEmbedding() {
        return embedding;
    }

    public void setEmbedding(Embedding embedding) {
        this.embedding = embedding;
    }

    public Retrieval getRetrieval() {
        return retrieval;
    }

    public void setRetrieval(Retrieval retrieval) {
        this.retrieval = retrieval;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    public static class Embedding {
        @Min(1)
        private int dimensions = 3072;

        public int getDimensions() {
            return dimensions;
        }

        public void setDimensions(int dimensions) {
            this.dimensions = dimensions;
        }
    }

    public static class Retrieval {
        @Min(1)
        private int topK = 6;
        @Min(1)
        private int maxChunks = 6;
        @Min(100)
        private int maxContextChars = 12000;
        private Double maxDistance;

        public int getTopK() {
            return topK;
        }

        public void setTopK(int topK) {
            this.topK = topK;
        }

        public int getMaxChunks() {
            return maxChunks;
        }

        public void setMaxChunks(int maxChunks) {
            this.maxChunks = maxChunks;
        }

        public int getMaxContextChars() {
            return maxContextChars;
        }

        public void setMaxContextChars(int maxContextChars) {
            this.maxContextChars = maxContextChars;
        }

        public Double getMaxDistance() {
            return maxDistance;
        }

        public void setMaxDistance(Double maxDistance) {
            this.maxDistance = maxDistance;
        }
    }

    public static class Chunk {
        @Min(1)
        private int sizeTokens = 800;
        @Min(0)
        private int overlapTokens = 100;

        public int getSizeTokens() {
            return sizeTokens;
        }

        public void setSizeTokens(int sizeTokens) {
            this.sizeTokens = sizeTokens;
        }

        public int getOverlapTokens() {
            return overlapTokens;
        }

        public void setOverlapTokens(int overlapTokens) {
            this.overlapTokens = overlapTokens;
        }
    }
}

package com.usai.rag.api.dto;

public class IngestResponse {
    private String status;
    private int documents;
    private int chunks;
    private int errors;
    private long latencyMs;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDocuments() {
        return documents;
    }

    public void setDocuments(int documents) {
        this.documents = documents;
    }

    public int getChunks() {
        return chunks;
    }

    public void setChunks(int chunks) {
        this.chunks = chunks;
    }

    public int getErrors() {
        return errors;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }

    public long getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(long latencyMs) {
        this.latencyMs = latencyMs;
    }
}

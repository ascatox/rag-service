package com.usai.rag.api.dto;

import java.util.List;

public class AskResponse {
    private String answer;
    private List<Citation> citations;
    private Confidence confidence;
    private long latencyMs;
    private TokenUsage tokenUsage;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<Citation> getCitations() {
        return citations;
    }

    public void setCitations(List<Citation> citations) {
        this.citations = citations;
    }

    public Confidence getConfidence() {
        return confidence;
    }

    public void setConfidence(Confidence confidence) {
        this.confidence = confidence;
    }

    public long getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(long latencyMs) {
        this.latencyMs = latencyMs;
    }

    public TokenUsage getTokenUsage() {
        return tokenUsage;
    }

    public void setTokenUsage(TokenUsage tokenUsage) {
        this.tokenUsage = tokenUsage;
    }

    public enum Confidence {
        LOW,
        MED,
        HIGH
    }
}

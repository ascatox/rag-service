package com.usai.rag.model;

public record ChunkInput(
    String content,
    int tokenCount,
    String section,
    Integer lineStart,
    Integer lineEnd
) {}

package com.usai.rag.model;

public record ChunkResult(
    String path,
    String section,
    Integer lineStart,
    Integer lineEnd,
    String content,
    Double distance
) {}

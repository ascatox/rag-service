package com.usai.rag.model;

import java.nio.file.Path;
import java.time.Instant;

public record DocumentInput(
    Path path,
    String contentType,
    Instant lastModified,
    String content
) {}

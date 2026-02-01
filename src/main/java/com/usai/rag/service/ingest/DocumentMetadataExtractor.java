package com.usai.rag.service.ingest;

import java.nio.file.Path;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DocumentMetadataExtractor {

    public Map<String, Object> extract(Path path) {
        // Simple heuristic: parse path segments like docs/<domain>/<service>/...
        String normalized = path.toString().replace('\\', '/');
        String[] parts = normalized.split("/");
        String service = null;
        String environment = null;

        for (int i = 0; i < parts.length; i++) {
            if ("services".equalsIgnoreCase(parts[i]) && i + 1 < parts.length) {
                service = parts[i + 1];
            }
            if ("env".equalsIgnoreCase(parts[i]) && i + 1 < parts.length) {
                environment = parts[i + 1];
            }
        }

        return Map.of(
            "service", service,
            "environment", environment
        );
    }
}

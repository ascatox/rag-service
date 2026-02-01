package com.usai.rag.config;

import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.OpenTelemetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {

    @Bean
    public Tracer ragTracer(OpenTelemetry openTelemetry) {
        return openTelemetry.getTracer("com.usai.rag");
    }
}

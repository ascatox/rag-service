package com.usai.rag.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObservabilityConfig {

    @Bean
    public MeterBinder ragMetricsBinder() {
        return new MeterBinder() {
            @Override
            public void bindTo(MeterRegistry registry) {
                // Placeholder for registering custom meters if needed.
            }
        };
    }
}

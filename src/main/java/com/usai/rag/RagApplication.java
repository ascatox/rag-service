package com.usai.rag;

import com.usai.rag.config.OpenAiProperties;
import com.usai.rag.config.RagProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({OpenAiProperties.class, RagProperties.class})
public class RagApplication {
    public static void main(String[] args) {
        SpringApplication.run(RagApplication.class, args);
    }
}

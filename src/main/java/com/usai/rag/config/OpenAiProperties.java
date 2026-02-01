package com.usai.rag.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "openai")
@Validated
public class OpenAiProperties {
    @NotBlank
    private String apiKey;
    private String baseUrl;
    private Model model = new Model();

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public static class Model {
        @NotBlank
        private String chat;
        @NotBlank
        private String embeddings;

        public String getChat() {
            return chat;
        }

        public void setChat(String chat) {
            this.chat = chat;
        }

        public String getEmbeddings() {
            return embeddings;
        }

        public void setEmbeddings(String embeddings) {
            this.embeddings = embeddings;
        }
    }
}

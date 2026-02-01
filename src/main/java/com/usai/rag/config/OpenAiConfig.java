package com.usai.rag.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class OpenAiConfig {

    @Bean
    public ChatModel chatLanguageModel(OpenAiProperties props) {
        OpenAiChatModel.OpenAiChatModelBuilder builder = OpenAiChatModel.builder()
            .apiKey(props.getApiKey())
            .modelName(props.getModel().getChat());

        if (StringUtils.hasText(props.getBaseUrl())) {
            builder.baseUrl(props.getBaseUrl());
        }

        return builder.build();
    }

    @Bean
    public EmbeddingModel embeddingModel(OpenAiProperties props) {
        OpenAiEmbeddingModel.OpenAiEmbeddingModelBuilder builder = OpenAiEmbeddingModel.builder()
            .apiKey(props.getApiKey())
            .modelName(props.getModel().getEmbeddings());

        if (StringUtils.hasText(props.getBaseUrl())) {
            builder.baseUrl(props.getBaseUrl());
        }

        return builder.build();
    }
}

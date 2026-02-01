package com.usai.rag;

import com.usai.rag.api.dto.AskRequest;
import com.usai.rag.api.dto.AskResponse;
import com.usai.rag.api.dto.IngestRequest;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.data.segment.TextSegment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class RagE2ETest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg16")
        .withDatabaseName("rag")
        .withUsername("rag")
        .withPassword("rag");

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("rag.embedding.dimensions", () -> "3");
        registry.add("openai.apiKey", () -> "test");
        registry.add("openai.model.chat", () -> "test-chat");
        registry.add("openai.model.embeddings", () -> "test-emb");
    }

    @TestConfiguration
    static class TestBeans {
        @Bean
        @Primary
        EmbeddingModel embeddingModel() {
            return new EmbeddingModel() {
                @Override
                public Response<java.util.List<Embedding>> embedAll(java.util.List<TextSegment> textSegments) {
                    return Response.from(java.util.List.of(new Embedding(new float[]{1f, 0f, 0f})));
                }

                @Override
                public Response<Embedding> embed(String text) {
                    return Response.from(new Embedding(new float[]{1f, 0f, 0f}));
                }
            };
        }

        @Bean
        @Primary
        ChatModel chatLanguageModel() {
            return new ChatModel() {
                @Override
                public String chat(String prompt) {
                    return "Risposta di test [1]";
                }
            };
        }
    }

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void ingestAndAskFlow(@TempDir Path tempDir) throws Exception {
        Path doc = tempDir.resolve("runbook.md");
        Files.writeString(doc, "line1\nline2\nline3\n");

        IngestRequest ingestRequest = new IngestRequest();
        ingestRequest.setPath(tempDir.toString());
        ingestRequest.setMode(IngestRequest.Mode.FULL);

        var ingestResp = mockMvc.perform(post("/ingest")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(ingestRequest)))
            .andExpect(status().is2xxSuccessful())
            .andReturn();

        AskRequest askRequest = new AskRequest();
        askRequest.setQuestion("Che cosa c'e' nella riga 2?");
        askRequest.setMode(AskRequest.Mode.CONCISE);
        askRequest.setTopK(3);

        var askResp = mockMvc.perform(post("/ask")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(askRequest)))
            .andExpect(status().is2xxSuccessful())
            .andReturn();
        AskResponse body = objectMapper.readValue(askResp.getResponse().getContentAsString(), AskResponse.class);
        assertThat(body).isNotNull();
        assertThat(body.getAnswer()).contains("[1]");
        assertThat(body.getCitations()).isNotEmpty();
        assertThat(body.getCitations().get(0).getLineStart()).isGreaterThan(0);
    }
}

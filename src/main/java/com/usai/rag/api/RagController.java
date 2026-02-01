package com.usai.rag.api;

import com.usai.rag.api.dto.AskRequest;
import com.usai.rag.api.dto.AskResponse;
import com.usai.rag.api.dto.IngestRequest;
import com.usai.rag.api.dto.IngestResponse;
import com.usai.rag.service.AskService;
import com.usai.rag.service.IngestService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class RagController {
    private final AskService askService;
    private final IngestService ingestService;

    public RagController(AskService askService, IngestService ingestService) {
        this.askService = askService;
        this.ingestService = ingestService;
    }

    @PostMapping(path = "/ask", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AskResponse ask(@Valid @RequestBody AskRequest request) {
        long start = System.nanoTime();
        AskResponse response = askService.ask(request, 0L);
        long latencyMs = (System.nanoTime() - start) / 1_000_000L;
        response.setLatencyMs(latencyMs);
        return response;
    }

    @PostMapping(path = "/ingest", consumes = MediaType.APPLICATION_JSON_VALUE)
    public IngestResponse ingest(@Valid @RequestBody IngestRequest request) {
        long start = System.nanoTime();
        IngestResponse response = ingestService.ingest(request, 0L);
        long latencyMs = (System.nanoTime() - start) / 1_000_000L;
        response.setLatencyMs(latencyMs);
        return response;
    }
}

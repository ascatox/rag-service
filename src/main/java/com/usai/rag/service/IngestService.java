package com.usai.rag.service;

import com.usai.rag.api.dto.IngestRequest;
import com.usai.rag.api.dto.IngestResponse;

public interface IngestService {
    IngestResponse ingest(IngestRequest request, long latencyMs);
}

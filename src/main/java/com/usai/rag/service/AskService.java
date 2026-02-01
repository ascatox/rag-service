package com.usai.rag.service;

import com.usai.rag.api.dto.AskRequest;
import com.usai.rag.api.dto.AskResponse;

public interface AskService {
    AskResponse ask(AskRequest request, long latencyMs);
}

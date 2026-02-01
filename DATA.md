# DATA — AI Runbook Assistant (RAG) — Contratti API e Modello Dati

## API

### POST /ask
**Request**
```json
{
  "question": "string",
  "filters": {
    "service": "string",
    "tag": ["string"],
    "environment": "string"
  },
  "topK": 6,
  "mode": "concise"
}
```

**Response**
```json
{
  "answer": "string",
  "citations": [
    {
      "file": "docs/runbooks/payments.md",
      "section": "Rollback",
      "lineStart": 120,
      "lineEnd": 145
    }
  ],
  "confidence": "high",
  "latencyMs": 1234,
  "tokenUsage": {
    "input": 1234,
    "output": 456,
    "total": 1690
  }
}
```

**Note**
- `mode`: `concise` | `detailed` | `checklist`.
- `topK` default 6.
- `filters` opzionale.
- Se retrieval vuoto o insufficienti fonti: risposta con `answer` che indica mancanza di info e `citations` vuoto, `confidence` = `low`.
- `citations.lineStart`/`lineEnd` sempre valorizzati quando disponibili dal contenuto sorgente; se mancanti, usare `-1` come placeholder.

### POST /ingest
**Request**
```json
{
  "path": "docs/",
  "mode": "full",
  "include": ["**/*.md", "**/*.txt", "**/*.pdf"],
  "exclude": ["**/node_modules/**"]
}
```

**Response**
```json
{
  "status": "ok",
  "documents": 120,
  "chunks": 1450,
  "errors": 2,
  "latencyMs": 5321
}
```

**Note**
- `mode`: `full` | `incremental`.
- `path` può puntare a una cartella locale oppure a un file caricato.

### GET /health
**Response**
```json
{
  "status": "UP"
}
```

### GET /metrics
- Endpoint Prometheus/Micrometer standard.

## Errori API (schema comune)
```json
{
  "error": {
    "code": "string",
    "message": "string",
    "details": "string"
  }
}
```

## Modello Dati (concettuale)

### Document
- `id` (UUID)
- `path` (string)
- `fileName` (string)
- `contentType` (string)
- `lastModified` (timestamp)
- `checksum` (string)
- `tags` (array)
- `service` (string)
- `environment` (string)

### Chunk
- `id` (UUID)
- `documentId` (UUID, FK)
- `content` (text)
- `tokenCount` (int)
- `section` (string)
- `lineStart` (int)
- `lineEnd` (int)
- `embedding` (vector)
- `metadata` (jsonb)

### IngestionRun
- `id` (UUID)
- `mode` (full | incremental)
- `startedAt` (timestamp)
- `finishedAt` (timestamp)
- `documentsProcessed` (int)
- `chunksCreated` (int)
- `errors` (int)

## Metriche chiave (nome indicativo)
- `http_server_requests_seconds_*`
- `rag_ingest_latency_seconds`
- `rag_ingest_documents_total`
- `rag_ingest_errors_total`
- `rag_retrieval_latency_seconds`
- `rag_retrieval_hits_total`
- `rag_llm_latency_seconds`
- `rag_llm_errors_total`

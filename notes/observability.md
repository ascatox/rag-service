# Observability — Design notes

## Metrics
- `rag_ingest_latency_seconds`
- `rag_ingest_documents_total`
- `rag_ingest_errors_total`
- `rag_retrieval_latency_seconds`
- `rag_retrieval_hits_total`
- `rag_llm_latency_seconds`
- `rag_llm_errors_total`
- Standard `http_server_requests_seconds_*` and JVM metrics

## Logs
- Structured logs with fields: `requestId`, `questionHash`, `topK`, `filters`, `docIds`, `chunkCount`, `latencyMs`, `errorCode`.
- Log sampling or redaction for PII (avoid full prompts in prod).

## Traces
- Span per endpoint (`/ask`, `/ingest`), with child spans:
  - `embed_query`, `vector_search`, `llm_generate`, `ingest_parse`, `ingest_embed`, `db_write`.
- Correlation via `traceId` in logs.

## Open questions
- Do we need to expose **custom Prometheus metrics** or only Micrometer defaults?
- Should we implement **request ID propagation** middleware now or later?


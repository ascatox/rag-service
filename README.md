# AI Runbook Assistant (RAG)

## Quick start

### Requirements
- Java 17 (current dev default; can be bumped later).
- Docker (optional) for E2E tests with Testcontainers.

### Configure
- Copy `config/app.example.yml` to `config/app.yml` and set OpenAI + DB settings.
- (Optional) use `.env.example` as reference for env vars.

### Run tests
```bash
mvn test
```

Notes:
- E2E test `RagE2ETest` is skipped automatically when Docker is not available.
- If Docker is available, it will spin up `pgvector/pgvector:pg16` via Testcontainers.

## Dev notes
- `SPECS.md`, `ARCH.md`, `DATA.md` contain product/design specs.
- `notes/` contains implementation notes (ingest, retrieval, DB schema, observability).
- `ACTIVITY_LOG.md` tracks chronological changes.

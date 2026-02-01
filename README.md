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

## Cosa fa questo servizio
Questo microservizio fornisce un assistente RAG per il team backend: indicizziamo documenti operativi (runbook, ADR, README, FAQ) e rispondiamo a domande tecniche **solo** usando le fonti disponibili.  
L’API `/ask` restituisce risposte con citazioni (file + linee), una stima di confidenza e latenza; se non trova informazioni, lo dichiara esplicitamente.  
L’API `/ingest` carica i documenti, li suddivide in chunk, genera embeddings e li salva su PostgreSQL + pgvector per la ricerca semantica.  
Il servizio include osservabilità (metriche, tracing), protezioni base contro prompt injection e una modalità di risposta configurabile (concise/detailed/checklist).

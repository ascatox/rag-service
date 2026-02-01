# ARCH — AI Runbook Assistant (RAG) — Architettura

## Obiettivo
Microservizio RAG per rispondere a domande operative/tecniche usando documentazione interna con risposte grounded e citazioni.

## Stack
- Java: usare l'ultima versione disponibile al momento dell'implementazione.
- Spring Boot: usare l'ultima versione disponibile al momento dell'implementazione.
- LangChain4j
- OpenAI (LLM + embeddings)
- PostgreSQL + pgvector
- Micrometer + OpenTelemetry

## Componenti principali
- **API layer (Spring Boot)**: espone `/ask`, `/ingest`, `/health`, `/metrics`.
- **Ingestion pipeline**: loader → parser → chunker → embedder → vector store.
- **Retrieval service**: query embedding → topK search → opz. rerank → context builder.
- **Answer service**: prompt builder + LLM call + citazioni + confidence.
- **Storage**: Postgres + pgvector per embeddings e metadata; file system per sorgenti raw (opzionale).
- **Observability**: logging strutturato, tracing (OpenTelemetry), metrics (Micrometer).

## Flusso /ingest
1. **Input**: path locale o upload file + modalità (full/incremental).
2. **Parsing**: Markdown/TXT/PDF (estrazione testo) + metadata base (file, path, lastModified, tag/service/environment).
3. **Chunking**: 500–1000 token, overlap 50–150.
4. **Embedding**: OpenAI embeddings.
5. **Persistenza**: salvare chunk + metadata + embedding su Postgres/pgvector.
6. **Output**: stats ingest (documenti, chunk, errori).

## Flusso /ask
1. **Input**: question + filters + topK + mode.
2. **Pre-processing**: sanitize + anti-injection guard.
3. **Retrieval**: embedding domanda → vector search → topK chunk.
4. **Context**: composizione contesto con citazioni (file + sezione/linee).
5. **LLM**: prompt con istruzioni grounding + risposta.
6. **Post-processing**: formattazione secondo `mode`, citazioni obbligatorie, confidence.
7. **Output**: answer + citations[] + confidence + latency + tokenUsage.

## Osservabilita
- **Metrics**: latency per endpoint, LLM latency, retrieval hit-rate, errori ingest.
- **Logs**: request id, query hash, topK, doc ids, esito grounding.
- **Tracing**: pipeline ingest e ask (span per embedding, search, LLM).


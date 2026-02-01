# Release Checklist

- [ ] Java version confirmed (currently 17 in `pom.xml`)
- [ ] OpenAI API key configured in `config/app.yml`
- [ ] Postgres + pgvector available (docker-compose or external)
- [ ] Liquibase migrations applied successfully
- [ ] `/ingest` smoke test on sample docs
- [ ] `/ask` smoke test with citations
- [ ] Prometheus metrics endpoint reachable (`/actuator/prometheus`)
- [ ] Tracing pipeline (OTLP) configured if needed
- [ ] E2E tests with Docker: `mvn test`
- [ ] Update README with deployment instructions

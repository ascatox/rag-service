# rag-ui (Angular)

This folder is a manual skeleton for an Angular app (CLI scaffold pending).
Standalone component files are already prepared under `src/app/`.

## Next steps (when npm registry access is available)
```bash
npx -y @angular/cli@latest new rag-ui --directory frontend --style scss --routing --standalone --ssr false --skip-git --strict
```

Then reapply the prepared standalone component files under `src/app/` and `src/main.ts`, then run:
```bash
npm install
npm start
```

## Intended features
- Quick Ask form -> POST `/ask`
- Ingest form -> POST `/ingest`
- Results panel with citations and line ranges
- Mode selector (concise/detailed/checklist)
- Filters (service, environment, tags)

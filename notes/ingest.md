# Ingestion pipeline — Design notes

## Design summary
- **Input sources**: local filesystem path (folder or single file). Support patterns via include/exclude globs. Default to `docs/**` tree.
- **Parsing**: detect by extension/content-type. `.md`/`.txt` read as UTF‑8; `.pdf` extracted via PDFBox.
- **Chunking**: character‑based windows approximating tokens (≈4 chars/token), configurable `rag.chunk.sizeTokens` and `rag.chunk.overlapTokens`. Compute `lineStart/lineEnd` from original text for citations.
- **Metadata**: base metadata from path and file stats (path, fileName, contentType, lastModified, checksum). Optional heuristics for `service`/`environment` from path segments; tags initially empty or derived from folders.
- **Incremental ingest**: compare `checksum` and/or `lastModified` in `documents` table. Skip documents where checksum matches (no re‑embed), else delete old chunks and re‑insert.

## Edge cases
- **Large PDFs**: PDF extraction can be slow; consider per‑page extraction and truncation limits.
- **Binary/unknown types**: skip with warning; do not ingest.
- **Globs with absolute paths**: match relative paths to root to avoid accidental exclusion.
- **Line numbers**: PDF line numbering is approximate (text extractor output); note in citations if needed.
- **Checksum collisions**: extremely unlikely; safe to treat as unchanged.

## Open questions
- Do we want a **max doc size** / max chunks per document guardrail?
- How should we extract **sections** (e.g., Markdown headings) for more precise citations?
- Should incremental mode use **deleted file cleanup** (remove DB docs not present on disk)?


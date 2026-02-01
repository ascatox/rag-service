# DB schema — Design notes

## Tables and columns
- **documents**
  - `id` (uuid, PK)
  - `path` (text, unique)
  - `file_name` (text)
  - `content_type` (text)
  - `last_modified` (timestamptz)
  - `checksum` (text)
  - `tags` (text[])
  - `service` (text)
  - `environment` (text)
  - `created_at` (timestamptz)

- **chunks**
  - `id` (uuid, PK)
  - `document_id` (uuid, FK → documents.id)
  - `content` (text)
  - `token_count` (int)
  - `section` (text)
  - `line_start` (int)
  - `line_end` (int)
  - `embedding` (vector(${embeddingDimensions}))
  - `metadata` (jsonb)
  - `created_at` (timestamptz)

- **ingestion_runs**
  - `id` (uuid, PK)
  - `mode` (text)
  - `started_at` (timestamptz)
  - `finished_at` (timestamptz)
  - `documents_processed` (int)
  - `chunks_created` (int)
  - `errors` (int)
  - `created_at` (timestamptz)

## Indexes
- `documents.path` unique.
- `chunks.document_id` for joins.
- `chunks.section` (optional for filtering).
- `chunks.embedding` with pgvector index (`gist` or `ivfflat` in prod).

## Migration notes
- Use Liquibase to enable `vector` extension.
- Keep `embeddingDimensions` as parameter for model changes.
- Consider adding `updated_at` if incremental ingestion needs tracking.
- For large datasets, switch `gist` to `ivfflat` and tune lists/probes.


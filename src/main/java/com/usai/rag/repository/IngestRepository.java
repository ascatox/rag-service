package com.usai.rag.repository;

import com.usai.rag.model.ChunkInput;
import com.usai.rag.model.DocumentInput;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class IngestRepository {
    private final JdbcTemplate jdbcTemplate;

    public IngestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public UUID upsertDocument(DocumentInput doc, String checksum, List<String> tags, String service, String environment) {
        UUID existing = jdbcTemplate.query(
            "SELECT id FROM documents WHERE path = ?",
            rs -> rs.next() ? UUID.fromString(rs.getString("id")) : null,
            doc.path().toString()
        );
        if (existing != null) {
            jdbcTemplate.update(
                "UPDATE documents SET file_name=?, content_type=?, last_modified=?, checksum=?, tags=?, service=?, environment=? WHERE id=?",
                doc.path().getFileName().toString(),
                doc.contentType(),
                doc.lastModified() != null ? Instant.from(doc.lastModified()) : null,
                checksum,
                toSqlArray(tags),
                service,
                environment,
                existing
            );
            return existing;
        }

        UUID id = UUID.randomUUID();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO documents (id, path, file_name, content_type, last_modified, checksum, tags, service, environment) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setObject(1, id);
            ps.setString(2, doc.path().toString());
            ps.setString(3, doc.path().getFileName().toString());
            ps.setString(4, doc.contentType());
            ps.setObject(5, doc.lastModified());
            ps.setString(6, checksum);
            ps.setArray(7, toSqlArray(tags));
            ps.setString(8, service);
            ps.setString(9, environment);
            return ps;
        });
        return id;
    }

    public void deleteChunksByDocument(UUID documentId) {
        jdbcTemplate.update("DELETE FROM chunks WHERE document_id = ?", documentId);
    }

    public void insertChunks(UUID documentId, List<ChunkInput> chunks, List<float[]> embeddings) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO chunks (id, document_id, content, token_count, section, line_start, line_end, embedding, metadata) VALUES (?, ?, ?, ?, ?, ?, ?, ?::vector, ?::jsonb)",
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws java.sql.SQLException {
                    ChunkInput chunk = chunks.get(i);
                    UUID id = UUID.randomUUID();
                    ps.setObject(1, id);
                    ps.setObject(2, documentId);
                    ps.setString(3, chunk.content());
                    ps.setInt(4, chunk.tokenCount());
                    ps.setString(5, chunk.section());
                    ps.setObject(6, chunk.lineStart());
                    ps.setObject(7, chunk.lineEnd());
                    ps.setString(8, toPgVectorLiteral(embeddings.get(i)));
                    ps.setString(9, "{}");
                }

                @Override
                public int getBatchSize() {
                    return chunks.size();
                }
            }
        );
    }

    private Array toSqlArray(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        return jdbcTemplate.execute((ConnectionCallback<Array>) connection ->
            connection.createArrayOf("text", tags.toArray())
        );
    }

    private String toPgVectorLiteral(float[] vector) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vector.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(vector[i]);
        }
        sb.append(']');
        return sb.toString();
    }
}

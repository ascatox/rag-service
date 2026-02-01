package com.usai.rag.repository;

import com.usai.rag.api.dto.AskRequest;
import com.usai.rag.model.ChunkResult;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class ChunkRepository {
    private final JdbcTemplate jdbcTemplate;

    public ChunkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ChunkResult> searchSimilar(String vectorLiteral, int topK, AskRequest.Filters filters) {
        StringBuilder sql = new StringBuilder(
            "SELECT d.path, c.section, c.line_start, c.line_end, c.content, " +
            "c.embedding <-> ?::vector AS distance " +
            "FROM chunks c JOIN documents d ON c.document_id = d.id WHERE 1=1 "
        );
        List<Object> args = new ArrayList<>();
        args.add(vectorLiteral);

        if (filters != null) {
            if (StringUtils.hasText(filters.getService())) {
                sql.append(" AND d.service = ?");
                args.add(filters.getService());
            }
            if (StringUtils.hasText(filters.getEnvironment())) {
                sql.append(" AND d.environment = ?");
                args.add(filters.getEnvironment());
            }
            if (filters.getTag() != null && filters.getTag().length > 0) {
                sql.append(" AND d.tags && ?::text[]");
                args.add(toPgTextArray(filters.getTag()));
            }
        }

        sql.append(" ORDER BY distance ASC LIMIT ?");
        args.add(topK);

        return jdbcTemplate.query(sql.toString(), rowMapper(), args.toArray());
    }

    private RowMapper<ChunkResult> rowMapper() {
        return (rs, rowNum) -> new ChunkResult(
            rs.getString("path"),
            rs.getString("section"),
            (Integer) rs.getObject("line_start"),
            (Integer) rs.getObject("line_end"),
            rs.getString("content"),
            rs.getObject("distance") != null ? rs.getDouble("distance") : null
        );
    }

    private String toPgTextArray(String[] tags) {
        return "{" + String.join(",", tags) + "}";
    }
}

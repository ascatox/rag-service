package com.usai.rag;

import com.usai.rag.service.ingest.Chunker;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ChunkerTest {

    @Test
    void computesLineNumbers() {
        Chunker chunker = new Chunker();
        String content = "line1\nline2\nline3\n";

        var chunks = chunker.chunk(content, 50, 0);

        assertThat(chunks).isNotEmpty();
        var chunk = chunks.get(0);
        assertThat(chunk.lineStart()).isEqualTo(1);
        assertThat(chunk.lineEnd()).isGreaterThanOrEqualTo(1);
    }
}

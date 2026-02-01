package com.usai.rag.service.ingest;

import com.usai.rag.model.ChunkInput;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class Chunker {

    public List<ChunkInput> chunk(String content, int sizeTokens, int overlapTokens) {
        List<ChunkInput> chunks = new ArrayList<>();
        if (content == null || content.isBlank()) {
            return chunks;
        }

        int maxChars = Math.max(1, sizeTokens * 4);
        int overlapChars = Math.max(0, overlapTokens * 4);
        int[] lineStarts = computeLineStarts(content);

        int index = 0;
        int length = content.length();
        while (index < length) {
            int end = Math.min(length, index + maxChars);
            if (end < length) {
                int softEnd = findSoftBoundary(content, index, end);
                if (softEnd > index) {
                    end = softEnd;
                }
            }

            String chunkText = content.substring(index, end).trim();
            if (!chunkText.isEmpty()) {
                int tokenCount = estimateTokens(chunkText);
                int lineStart = lineNumberAt(lineStarts, index);
                int lineEnd = lineNumberAt(lineStarts, Math.max(index, end - 1));
                chunks.add(new ChunkInput(chunkText, tokenCount, null, lineStart, lineEnd));
            }

            if (end >= length) {
                break;
            }
            index = Math.max(end - overlapChars, 0);
        }

        return chunks;
    }

    private int estimateTokens(String text) {
        int chars = text.length();
        return Math.max(1, (int) Math.ceil(chars / 4.0));
    }

    private int findSoftBoundary(String content, int start, int hardEnd) {
        int window = Math.min(200, hardEnd - start);
        for (int i = hardEnd - 1; i >= hardEnd - window; i--) {
            char c = content.charAt(i);
            if (Character.isWhitespace(c)) {
                return i;
            }
        }
        return hardEnd;
    }

    private int[] computeLineStarts(String content) {
        List<Integer> starts = new ArrayList<>();
        starts.add(0);
        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) == '\n') {
                int next = i + 1;
                if (next < content.length()) {
                    starts.add(next);
                }
            }
        }
        int[] result = new int[starts.size()];
        for (int i = 0; i < starts.size(); i++) {
            result[i] = starts.get(i);
        }
        return result;
    }

    private int lineNumberAt(int[] lineStarts, int index) {
        int low = 0;
        int high = lineStarts.length - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int start = lineStarts[mid];
            if (start == index) {
                return mid + 1;
            }
            if (start < index) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return Math.max(1, high + 1);
    }
}

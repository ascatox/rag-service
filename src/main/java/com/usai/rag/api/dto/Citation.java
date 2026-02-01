package com.usai.rag.api.dto;

public class Citation {
    private String file;
    private String section;
    private Integer lineStart;
    private Integer lineEnd;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Integer getLineStart() {
        return lineStart;
    }

    public void setLineStart(Integer lineStart) {
        if (lineStart != null && lineStart < -1) {
            throw new IllegalArgumentException("lineStart must be >= -1");
        }
        this.lineStart = lineStart;
    }

    public Integer getLineEnd() {
        return lineEnd;
    }

    public void setLineEnd(Integer lineEnd) {
        if (lineEnd != null && lineEnd < -1) {
            throw new IllegalArgumentException("lineEnd must be >= -1");
        }
        this.lineEnd = lineEnd;
    }
}

package com.usai.rag.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class IngestRequest {
    @NotBlank
    private String path;

    @NotNull
    private Mode mode = Mode.FULL;

    private String[] include;
    private String[] exclude;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public String[] getInclude() {
        return include;
    }

    public void setInclude(String[] include) {
        this.include = include;
    }

    public String[] getExclude() {
        return exclude;
    }

    public void setExclude(String[] exclude) {
        this.exclude = exclude;
    }

    public enum Mode {
        FULL,
        INCREMENTAL
    }
}

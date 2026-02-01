package com.usai.rag.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public class AskRequest {
    @NotBlank
    private String question;

    @Valid
    private Filters filters;

    @Min(1)
    @Max(50)
    private Integer topK = 6;

    @NotNull
    private Mode mode = Mode.CONCISE;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Filters getFilters() {
        return filters;
    }

    public void setFilters(Filters filters) {
        this.filters = filters;
    }

    public Integer getTopK() {
        return topK;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public enum Mode {
        CONCISE,
        DETAILED,
        CHECKLIST
    }

    public static class Filters {
        private String service;
        private String environment;
        private String[] tag;
        private Map<String, String> extra;

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public String getEnvironment() {
            return environment;
        }

        public void setEnvironment(String environment) {
            this.environment = environment;
        }

        public String[] getTag() {
            return tag;
        }

        public void setTag(String[] tag) {
            this.tag = tag;
        }

        public Map<String, String> getExtra() {
            return extra;
        }

        public void setExtra(Map<String, String> extra) {
            this.extra = extra;
        }
    }
}

package com.usai.rag.api.dto;

public class TokenUsage {
    private Integer input;
    private Integer output;
    private Integer total;

    public Integer getInput() {
        return input;
    }

    public void setInput(Integer input) {
        this.input = input;
    }

    public Integer getOutput() {
        return output;
    }

    public void setOutput(Integer output) {
        this.output = output;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}

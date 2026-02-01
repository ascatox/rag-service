package com.usai.rag.api.error;

public class ErrorResponse {
    private ErrorBody error;

    public ErrorResponse() {}

    public ErrorResponse(ErrorBody error) {
        this.error = error;
    }

    public ErrorBody getError() {
        return error;
    }

    public void setError(ErrorBody error) {
        this.error = error;
    }

    public static class ErrorBody {
        private String code;
        private String message;
        private String details;

        public ErrorBody() {}

        public ErrorBody(String code, String message, String details) {
            this.code = code;
            this.message = message;
            this.details = details;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }
    }
}

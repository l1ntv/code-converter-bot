package ru.rsreu.lint.response;

import ru.rsreu.lint.enums.ResponseType;

public class BotResponse {
    private final ResponseType type;
    private final String message;

    public BotResponse(ResponseType type, String message) {
        this.type = type;
        this.message = message;
    }

    public ResponseType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
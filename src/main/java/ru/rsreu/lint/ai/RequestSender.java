package ru.rsreu.lint.ai;

import chat.giga.client.GigaChatClient;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.ModelName;
import chat.giga.model.completion.ChatMessage;
import chat.giga.model.completion.ChatMessageRole;
import chat.giga.model.completion.CompletionRequest;
import chat.giga.model.completion.CompletionResponse;

public class RequestSender {

    public CompletionResponse send(GigaChatClient client, String message) throws HttpClientException {
        return client.completions(CompletionRequest.builder()
                .model(ModelName.GIGA_CHAT_MAX)
                .message(ChatMessage.builder()
                        .content(message)
                        .role(ChatMessageRole.USER)
                        .build())
                .build());
    }
}

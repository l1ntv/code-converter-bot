package ru.rsreu.lint.ai;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder;
import chat.giga.model.Scope;

public class GigaChatClientRegister {

    public static GigaChatClient register() {
        String token = System.getenv("AI_TOKEN");
        return GigaChatClient.builder()
                .verifySslCerts(false)
                .authClient(AuthClient.builder()
                        .withOAuth(AuthClientBuilder.OAuthBuilder.builder()
                                .scope(Scope.GIGACHAT_API_PERS)
                                .authKey(token)
                                .build())
                        .build())
                .build();
    }
}

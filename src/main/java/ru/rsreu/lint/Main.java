package ru.rsreu.lint;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.rsreu.lint.request.CodeConverterBot;

public class Main {

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        String botToken = System.getenv("BOT_TOKEN");
        String botUsername = System.getenv("BOT_NAME");
        botsApi.registerBot(new CodeConverterBot(botToken, botUsername));
    }
}

package ru.rsreu.lint.request;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rsreu.lint.enums.BotMode;
import ru.rsreu.lint.enums.ResponseType;
import ru.rsreu.lint.response.BotResponse;
import ru.rsreu.lint.response.ResponseHandler;

public class CodeConverterBot extends TelegramLongPollingBot {

    private final String BOT_USERNAME;

    private final String BOT_TOKEN;

    private final ContentDefiner contentDefiner = new ContentDefiner();

    private final ResponseHandler responseHandler;

    public CodeConverterBot(String botToken, String botUsername) {
        this.BOT_TOKEN = botToken;
        this.BOT_USERNAME = botUsername;
        this.responseHandler = new ResponseHandler(this, contentDefiner);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (ResponseHandler.isExpectingCodeInput()) {
                String selectedLanguage = ResponseHandler.getSelectedLanguage();
                String selectedSqlDialect = ResponseHandler.getSelectedSqlDialect();
                BotMode mode = ResponseHandler.getCurrentMode();

                System.out.println("=== Получен код ===");
                System.out.println("Код:\n" + messageText);

                if (mode == BotMode.LANGUAGE_TRANSLATION) {
                    System.out.println("Режим: Перевод между ЯП");
                    System.out.println("Выбранный язык: " + formatLang(selectedLanguage));
                    System.out.println("Оптимизация: " + (ResponseHandler.isOptimize() ? "включена" : "выключена"));
                    System.out.println("Документация: " + (ResponseHandler.isDocumentation() ? "включена" : "выключена"));
                    System.out.println("Подсветка синтаксиса: " + (ResponseHandler.isSyntaxHighlight() ? "включена" : "выключена"));

                } else if (mode == BotMode.SQL_DIALECTS) {
                    System.out.println("Режим: Перевод между SQL-диалектами");
                    System.out.println("Выбранный диалект SQL: " + formatSqlDialect(selectedSqlDialect));
                    System.out.println("Комментарии: " + (ResponseHandler.isComments() ? "включены" : "выключены"));
                    System.out.println("Форматирование: " + (ResponseHandler.isFormatting() ? "включено" : "выключено"));
                    System.out.println("Регистр ключевых слов: " + (ResponseHandler.isKeywordCase() ? "включён" : "выключен"));
                }

                ResponseHandler.setExpectingCodeInput(false);
                try {
                    sendMessage(chatId, "Обработка запроса.");
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else {
                BotResponse response = contentDefiner.getResponse(messageText);
                if (response.getType() == ResponseType.START_MENU) {
                    try {
                        responseHandler.handleStartMenu(chatId);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

            try {
                responseHandler.handleCallback(chatId, messageId, callbackData);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendMessage(Long chatId, String text) throws TelegramApiException {
        var message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        execute(message);
    }

    private String formatLang(String langCode) {
        return langCode.substring(5).toUpperCase();
    }

    private String formatSqlDialect(String dialectCode) {
        return dialectCode.substring(12).toUpperCase().replace("_", " ");
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}

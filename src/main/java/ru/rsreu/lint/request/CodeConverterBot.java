package ru.rsreu.lint.request;

import chat.giga.client.GigaChatClient;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rsreu.lint.ai.GigaChatClientRegister;
import ru.rsreu.lint.ai.PromtFormer;
import ru.rsreu.lint.ai.RequestSender;
import ru.rsreu.lint.ai.ResponseFormater;
import ru.rsreu.lint.enums.BotMode;
import ru.rsreu.lint.enums.ResponseType;
import ru.rsreu.lint.response.BotResponse;
import ru.rsreu.lint.response.ResponseHandler;

import java.util.ArrayList;
import java.util.List;

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
                BotMode mode = ResponseHandler.getCurrentMode();

                GigaChatClient gigaChatClient = GigaChatClientRegister.register();
                String prompt = PromtFormer.formProgrammingConvertPromt(
                        selectedLanguage,
                        messageText,
                        ResponseHandler.isOptimize(),
                        ResponseHandler.isDocumentation(),
                        ResponseHandler.isStandards(),
                        ResponseHandler.isTests()
                );

                RequestSender requestSender = new RequestSender();

                try {
                    String aiResponse = requestSender.send(gigaChatClient, prompt).toString();
                    String formattedResponse = ResponseFormater.formatGigaChatResponse(aiResponse);

                    // Отправляем результат пользователю с кнопкой "Вернуться в меню"
                    sendMessageWithBackButton(chatId, formattedResponse);

                    ResponseHandler.setExpectingCodeInput(false);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else {
                BotResponse response = contentDefiner.getResponse(messageText);
                if (response.getType() == ResponseType.START_MENU) {
                    try {
                        responseHandler.handleStartMenu(chatId);
                    } catch (TelegramApiException ex) {
                        ex.printStackTrace();
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
                e.printStackTrace();
            }
        }
    }

    private void sendMessageWithBackButton(Long chatId, String aiResponse) throws TelegramApiException {
        var message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(aiResponse);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton backButton = new InlineKeyboardButton("⬅️ Вернуться в меню");
        backButton.setCallbackData("back_to_menu");

        rows.add(List.of(backButton));
        markup.setKeyboard(rows);

        message.setReplyMarkup(markup);
        execute(message);
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
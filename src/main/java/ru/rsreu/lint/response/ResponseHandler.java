package ru.rsreu.lint.response;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rsreu.lint.request.ContentDefiner;
import ru.rsreu.lint.enums.BotMode;

import java.util.ArrayList;
import java.util.List;

public class ResponseHandler {

    private final AbsSender absSender;

    private final ContentDefiner contentDefiner;

    private static String selectedLanguage = "lang_java";

    private static boolean optimize = true;

    private static boolean documentation = true;

    private static boolean syntaxHighlight = true;

    private static String selectedSqlDialect = "sql_dialect_postgresql";

    private static boolean comments = true;

    private static boolean formatting = true;

    private static boolean keywordCase = true;

    private static boolean expectingCodeInput = true;

    private static BotMode currentMode = BotMode.NONE;

    public ResponseHandler(AbsSender absSender, ContentDefiner contentDefiner) {
        this.absSender = absSender;
        this.contentDefiner = contentDefiner;
    }

    public void handleStartMenu(Long chatId) throws TelegramApiException {
        String text = "Меню:";
        InlineKeyboardMarkup markup = createMainMenuKeyboard();
        sendMessage(chatId, text, markup);
    }

    public void handleCallback(Long chatId, Integer messageId, String callbackData) throws TelegramApiException {
        BotResponse response = contentDefiner.getResponse(callbackData);

        switch (response.getType()) {
            case SELECTED_LANGUAGE -> updateSelectedLanguage(chatId, messageId, callbackData);
            case TOGGLE_OPTION -> toggleOption(chatId, messageId, callbackData);
            case SELECTED_SQL_DIALECT -> updateSelectedSqlDialect(chatId, messageId, callbackData);
            case SQL_TOGGLE_OPTION -> toggleSqlOption(chatId, messageId, callbackData);
            case CONTINUE -> sendCodeInputMessage(chatId);
            case BACK_TO_MENU -> goBackToMainMenu(chatId, messageId);
            case LANGUAGE_TRANSLATION -> showChooseLanguageAndOptions(chatId);
            case SQL_DIALECTS -> showChooseSqlDialectAndOptions(chatId);
            case ABOUT_BOT -> showAboutBot(chatId);
            default -> editMessage(chatId, messageId, "Неизвестная команда.");
        }
    }

    private void showChooseLanguageAndOptions(Long chatId) throws TelegramApiException {
        setCurrentMode(BotMode.LANGUAGE_TRANSLATION);
        String text = "Выберите ваш язык программирования и дополнительные параметры";
        InlineKeyboardMarkup markup = createLanguageOptionsKeyboard();
        sendMessage(chatId, text, markup);
    }

    private void showChooseSqlDialectAndOptions(Long chatId) throws TelegramApiException {
        setCurrentMode(BotMode.SQL_DIALECTS);
        String text = "Выберите ваш диалект SQL и дополнительные параметры";
        InlineKeyboardMarkup markup = createSqlDialectOptionsKeyboard();
        sendMessage(chatId, text, markup);
    }

    private void showAboutBot(Long chatId) throws TelegramApiException {
        setCurrentMode(BotMode.ABOUT);
        String text = contentDefiner.getResponse("about_bot").getMessage();
        InlineKeyboardMarkup markup = createAboutBotKeyboard();
        sendMessage(chatId, text, markup);
    }

    private InlineKeyboardMarkup createAboutBotKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton("⬅️ Назад");
        backButton.setCallbackData("back_to_menu");
        rows.add(List.of(backButton));
        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardMarkup createLanguageOptionsKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        String[] languages = {"Java", "Python", "JavaScript", "C++", "C#", "Go", "Ruby", "Swift", "Kotlin", "Rust"};
        for (String lang : languages) {
            String code = "lang_" + lang.toLowerCase();
            String emoji = selectedLanguage.equals(code) ? "✅" : "❌";
            InlineKeyboardButton button = new InlineKeyboardButton(emoji + " " + lang);
            button.setCallbackData(code);
            rows.add(List.of(button));
        }
        addToggleRow(rows, "toggle_optimize", "Оптимизация", optimize);
        addToggleRow(rows, "toggle_documentation", "Документация", documentation);
        addToggleRow(rows, "toggle_syntax_highlight", "Подсветка синтаксиса", syntaxHighlight);

        InlineKeyboardButton backButton = new InlineKeyboardButton("⬅️ Назад");
        backButton.setCallbackData("back_to_menu");

        InlineKeyboardButton continueButton = new InlineKeyboardButton("▶️ Продолжить");
        continueButton.setCallbackData("continue_to_code_input");

        rows.add(List.of(backButton, continueButton));
        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardMarkup createSqlDialectOptionsKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        String[] dialects = {"PostgreSQL", "MySQL", "MSSQL", "SQLite", "Oracle", "MariaDB", "SQL Server", "DB2", "Sybase", "Informix"};
        for (String dialect : dialects) {
            String code = "sql_dialect_" + dialect.toLowerCase().replace(" ", "_");
            String emoji = selectedSqlDialect.equals(code) ? "✅" : "❌";
            InlineKeyboardButton button = new InlineKeyboardButton(emoji + " " + dialect);
            button.setCallbackData(code);
            rows.add(List.of(button));
        }

        addToggleRow(rows, "sql_toggle_comments", "Комментарии", comments);
        addToggleRow(rows, "sql_toggle_formatting", "Форматирование", formatting);
        addToggleRow(rows, "sql_toggle_case", "Регистр ключевых слов", keywordCase);

        InlineKeyboardButton backButton = new InlineKeyboardButton("⬅️ Назад");
        backButton.setCallbackData("back_to_menu");

        InlineKeyboardButton continueButton = new InlineKeyboardButton("▶️ Продолжить");
        continueButton.setCallbackData("continue_to_code_input");

        rows.add(List.of(backButton, continueButton));
        markup.setKeyboard(rows);
        return markup;
    }

    private void addToggleRow(List<List<InlineKeyboardButton>> rows, String callback, String label, boolean state) {
        InlineKeyboardButton button = new InlineKeyboardButton((state ? "✅" : "❌") + " " + label);
        button.setCallbackData(callback);
        rows.add(List.of(button));
    }

    private void updateSelectedLanguage(Long chatId, Integer messageId, String callbackData) throws TelegramApiException {
        selectedLanguage = callbackData;
        editMessageWithUpdatedKeyboard(chatId, messageId);
    }

    private void toggleOption(Long chatId, Integer messageId, String callbackData) throws TelegramApiException {
        switch (callbackData) {
            case "toggle_optimize" -> optimize = !optimize;
            case "toggle_documentation" -> documentation = !documentation;
            case "toggle_syntax_highlight" -> syntaxHighlight = !syntaxHighlight;
        }
        editMessageWithUpdatedKeyboard(chatId, messageId);
    }

    private void updateSelectedSqlDialect(Long chatId, Integer messageId, String callbackData) throws TelegramApiException {
        selectedSqlDialect = callbackData;
        editSqlDialectWithUpdatedKeyboard(chatId, messageId);
    }

    private void toggleSqlOption(Long chatId, Integer messageId, String callbackData) throws TelegramApiException {
        switch (callbackData) {
            case "sql_toggle_comments" -> comments = !comments;
            case "sql_toggle_formatting" -> formatting = !formatting;
            case "sql_toggle_case" -> keywordCase = !keywordCase;
        }
        editSqlDialectWithUpdatedKeyboard(chatId, messageId);
    }

    private void editMessageWithUpdatedKeyboard(Long chatId, Integer messageId) throws TelegramApiException {
        String text = "Выберите ваш язык программирования и дополнительные параметры";
        InlineKeyboardMarkup markup = createLanguageOptionsKeyboard();

        var message = new EditMessageText();
        message.setChatId(chatId.toString());
        message.setMessageId(messageId);
        message.setText(text);
        message.setReplyMarkup(markup);
        absSender.execute(message);
    }

    private void editSqlDialectWithUpdatedKeyboard(Long chatId, Integer messageId) throws TelegramApiException {
        String text = "Выберите ваш диалект SQL и дополнительные параметры";
        InlineKeyboardMarkup markup = createSqlDialectOptionsKeyboard();

        var message = new EditMessageText();
        message.setChatId(chatId.toString());
        message.setMessageId(messageId);
        message.setText(text);
        message.setReplyMarkup(markup);
        absSender.execute(message);
    }

    private void goBackToMainMenu(Long chatId, Integer messageId) throws TelegramApiException {
        setCurrentMode(BotMode.NONE);
        String text = "Меню:";
        InlineKeyboardMarkup markup = createMainMenuKeyboard();
        editMessage(chatId, messageId, text, markup);
    }

    private void sendCodeInputMessage(Long chatId) throws TelegramApiException {
        String text = "Введите ваш код:";
        sendMessage(chatId, text);
        setExpectingCodeInput(true);
    }

    private void sendMessage(Long chatId, String text) throws TelegramApiException {
        var message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        absSender.execute(message);
    }

    private void sendMessage(Long chatId, String text, InlineKeyboardMarkup markup) throws TelegramApiException {
        var message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(markup);
        absSender.execute(message);
    }

    private void editMessage(Long chatId, Integer messageId, String text) throws TelegramApiException {
        var message = new EditMessageText();
        message.setChatId(chatId.toString());
        message.setMessageId(messageId);
        message.setText(text);
        absSender.execute(message);
    }

    private void editMessage(Long chatId, Integer messageId, String text, InlineKeyboardMarkup markup) throws TelegramApiException {
        var message = new EditMessageText();
        message.setChatId(chatId.toString());
        message.setMessageId(messageId);
        message.setText(text);
        message.setReplyMarkup(markup);
        absSender.execute(message);
    }

    private InlineKeyboardMarkup createMainMenuKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton("Перевод между ЯП");
        button1.setCallbackData("language_translation");

        InlineKeyboardButton button2 = new InlineKeyboardButton("Перевод между диалектами SQL");
        button2.setCallbackData("sql_dialects");

        InlineKeyboardButton button3 = new InlineKeyboardButton("Информация о боте");
        button3.setCallbackData("about_bot");

        rows.add(List.of(button1));
        rows.add(List.of(button2));
        rows.add(List.of(button3));
        markup.setKeyboard(rows);
        return markup;
    }

    public static String getSelectedLanguage() {
        return selectedLanguage;
    }

    public static boolean isOptimize() {
        return optimize;
    }

    public static boolean isDocumentation() {
        return documentation;
    }

    public static boolean isSyntaxHighlight() {
        return syntaxHighlight;
    }

    public static String getSelectedSqlDialect() {
        return selectedSqlDialect;
    }

    public static boolean isComments() {
        return comments;
    }

    public static boolean isFormatting() {
        return formatting;
    }

    public static boolean isKeywordCase() {
        return keywordCase;
    }

    public static boolean isExpectingCodeInput() {
        return expectingCodeInput;
    }

    public static void setExpectingCodeInput(boolean value) {
        expectingCodeInput = value;
    }

    public static BotMode getCurrentMode() {
        return currentMode;
    }

    public static void setCurrentMode(BotMode mode) {
        currentMode = mode;
    }
}

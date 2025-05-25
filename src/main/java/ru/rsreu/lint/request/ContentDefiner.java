package ru.rsreu.lint.request;

import ru.rsreu.lint.enums.ResponseType;
import ru.rsreu.lint.response.BotResponse;

public class ContentDefiner {

    public BotResponse getResponse(String command) {
        if (command.startsWith("lang_")) {
            return new BotResponse(ResponseType.SELECTED_LANGUAGE, "Выбран язык: " + formatLang(command));
        } else if (command.startsWith("toggle_")) {
            return new BotResponse(ResponseType.TOGGLE_OPTION, "Переключён параметр: " + formatOption(command));
        } else if (command.startsWith("sql_dialect_")) {
            return new BotResponse(ResponseType.SELECTED_SQL_DIALECT, "Выбран диалект SQL: " + formatSqlDialect(command));
        } else if (command.startsWith("sql_toggle_")) {
            return new BotResponse(ResponseType.SQL_TOGGLE_OPTION, "Переключён SQL-параметр: " + formatSqlOption(command));
        } else if ("continue_to_code_input".equals(command)) {
            return new BotResponse(ResponseType.CONTINUE, "Переход к вводу кода");
        } else if ("back_to_menu".equals(command)) {
            return new BotResponse(ResponseType.BACK_TO_MENU, "Возврат в главное меню");
        } else if ("done_language_options".equals(command)) {
            return new BotResponse(ResponseType.DONE, "Вы завершили выбор.");
        }

        return switch (command) {
            case "/start" -> new BotResponse(ResponseType.START_MENU, "Меню:");
            case "language_translation" -> new BotResponse(ResponseType.LANGUAGE_TRANSLATION, "Перевод между ЯП");
            case "sql_dialects" -> new BotResponse(ResponseType.SQL_DIALECTS, "Перевод между диалектами SQL");
            case "about_bot" -> new BotResponse(ResponseType.ABOUT_BOT, """
                    Это Telegram-бот для конвертации кода между языками программирования и SQL-диалектами.
                    
                    🔁 Поддерживает:
                    • Перевод между языками программирования: Java, Python, JavaScript, C++, C#, Go, Ruby, Swift, Kotlin, Rust
                    • Перевод между SQL-диалектами: PostgreSQL, MySQL, MSSQL, SQLite, Oracle, MariaDB, SQL Server, DB2, Sybase, Informix
                    
                    ⚙️ Дополнительные параметры:
                    • Оптимизация
                    • Генерация документации
                    • Форматирование
                    • Комментарии
                    • Регистр ключевых слов
                    
                    Используйте меню ниже.""");
            default -> new BotResponse(ResponseType.UNKNOWN, "Неизвестная команда.");
        };
    }

    private String formatLang(String langCode) {
        return langCode.substring(5).toUpperCase();
    }

    private String formatOption(String optionCode) {
        return switch (optionCode) {
            case "toggle_optimize" -> "Оптимизация";
            case "toggle_standards" -> "Стандарты";
            case "toggle_documentation" -> "Документация";
            case "toggle_tests" -> "Тесты";
            default -> "Неизвестный параметр";
        };
    }

    private String formatSqlDialect(String dialectCode) {
        return dialectCode.substring(12).toUpperCase().replace("_", " ");
    }

    private String formatSqlOption(String optionCode) {
        return switch (optionCode) {
            case "sql_toggle_comments" -> "Комментарии";
            case "sql_toggle_formatting" -> "Форматирование";
            case "sql_toggle_case" -> "Регистр ключевых слов";
            default -> "Неизвестный параметр";
        };
    }
}
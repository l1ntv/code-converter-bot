package ru.rsreu.lint.ai;

public class PromtFormer {

    public static String formProgrammingConvertPromt(String selectedLanguage,
                                                     String code,
                                                     boolean isOptimize,
                                                     boolean isDocumentation,
                                                     boolean isStandards,
                                                     boolean isTests) {
        StringBuilder message = new StringBuilder();
        message.append("Переведи код из одного языка программирования в другой. ")
                .append("Если текста запроса не код, то выведи сообщение 'Ошибка: сообщение не является кодом.'")
                .append("Пришли только полный переведенный код без каких-либо пояснений. ")
                .append("Необходимо перевести код на язык программирования: ")
                .append(selectedLanguage)
                .append(". Дополнительно выполни следующее:\n");
        if (isOptimize)
            message.append("Попробуй оптимизировать код.\n");
        if (isDocumentation)
            message.append("Напиши документацию (комментарии) к коду\n");
        if (isStandards)
            message.append("Придерживайся к стандартам языка, если он существует.");
        if (isTests)
            message.append("Напиши тесты к коду.\n");
        message.append("Этот код надо перевести: ").append(code).append("\n");
        return message.toString();
    }

    public static String formSqlConvertPromt(String selectedDialect,
                                             String query,
                                             boolean isComments,
                                             boolean isFormatting,
                                             boolean isKeywordCase) {
        StringBuilder message = new StringBuilder();
        message.append("Переведи запросы из одного диалетка SQL в другой. ")
                .append("Если текста запроса не код, то выведи сообщение 'Ошибка: сообщение не является кодом.'")
                .append("Пришли только полный переведенный код без каких-либо пояснений. ")
                .append("Необходимо перевести запрос на диалект SQL: ")
                .append(selectedDialect)
                .append(". Дополнительно выполни следующее:\n");
        if (isComments)
            message.append("Напиши комментарии.\n");
        if (isFormatting)
            message.append("Форматируй код\n");
        if (isKeywordCase)
            message.append("Учитывая регистр ключевых слов.\n");
        message.append("Этот код надо перевести: ").append(query).append("\n");
        return message.toString();
    }
}

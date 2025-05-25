package ru.rsreu.lint.ai;

public class ResponseFormater {

    public static String formatGigaChatResponse(String rawResponse) {
        if (rawResponse == null || rawResponse.isEmpty()) {
            return "Не получен результат от ИИ.";
        }

        try {
            // 1. Убираем префиксы вроде "content="
            String content = rawResponse;
            if (content.contains("content=")) {
                content = content.substring(content.indexOf("content=") + 8);
            }

            // 2. Убираем всё после "object=..." или "created=..."
            if (content.contains(", created")) {
                content = content.substring(0, content.indexOf(", created"));
            } else if (content.contains(", object")) {
                content = content.substring(0, content.indexOf(", object"));
            }

            // 3. Удаляем начальные/конечные пробелы
            content = content.trim();

            // 4. Убираем обратные кавычки и указание языка (``ruby, ``python и т.п.)
            content = content.replaceAll("```\\w+\n", "```\n");
            content = content.replace("```", "``````");

            // 5. Добавляем подпись
            return """
                    🧠 Ответ ИИ:
                    
                    %s
                    
                    """.formatted(content);

        } catch (Exception e) {
            return """
                    ❌ Не удалось обработать ответ ИИ.
                    Получено:
                    
                    %s
                    """.formatted(rawResponse);
        }
    }
}

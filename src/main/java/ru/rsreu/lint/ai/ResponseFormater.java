package ru.rsreu.lint.ai;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseFormater {

    public static String formatGigaChatResponse(String rawResponse) {
        if (rawResponse == null || rawResponse.isEmpty()) {
            return "❌ Не получен результат от ИИ.";
        }

        try {
            String content = rawResponse;
            if (content.contains("content=")) {
                content = content.substring(content.indexOf("content=") + "content=".length());
            }

            if (content.contains(", created")) {
                content = content.substring(0, content.indexOf(", created"));
            } else if (content.contains(", object")) {
                content = content.substring(0, content.indexOf(", object"));
            }

            content = content.trim().replaceAll("\n{3,}", "\n\n");
            content = removeExtraBackslashes(content);
            content = stripMarkdownMarkers(content);

            return """
                    🧠 Ответ ИИ:
                    
                    %s
                    """.formatted(content);
        } catch (Exception e) {
            e.printStackTrace();
            return """
                    ❌ Не удалось обработать ответ ИИ.
                    
                    Получено:
                    ```
                    %s
                    ```
                    """.formatted(rawResponse);
        }
    }

    private static String removeExtraBackslashes(String text) {
        return text.replace("\\\\#", "#")
                .replace("\\\\*", "*")
                .replace("\\\\(", "(")
                .replace("\\\\)", ")")
                .replace("\\\\[", "[")
                .replace("\\\\]", "]")
                .replace("\\\\.", ".")
                .replace("\\\\!", "!")
                .replace("\\\\-", "-")
                .replace("\\\\_", "_");
    }

    private static String stripMarkdownMarkers(String text) {
        StringBuilder result = new StringBuilder();
        Matcher matcher = Pattern.compile("(```.*?```)", Pattern.DOTALL).matcher(text);
        int lastEnd = 0;

        while (matcher.find()) {
            String before = text.substring(lastEnd, matcher.start());
            before = cleanTextFromMarkdown(before);
            result.append(before);
            result.append(matcher.group());
            lastEnd = matcher.end();
        }
        String tail = text.substring(lastEnd);
        result.append(cleanTextFromMarkdown(tail));
        return result.toString();
    }

    private static String cleanTextFromMarkdown(String text) {
        text = text.replaceAll("(?m)^\\s*#+.*$", "");
        text = text.replaceAll("(?m)^\\s*[-*]\\s+", "");
        text = text.replaceAll("(?m)^\\s*\\d+\\.\\s+", "");
        text = text.replaceAll("[*_~`]", "");
        text = text.replaceAll("\n{2,}", "\n\n").trim();
        return text;
    }
}

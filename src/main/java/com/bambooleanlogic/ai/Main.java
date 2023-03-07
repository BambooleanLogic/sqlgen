package com.bambooleanlogic.ai;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        SqlCode sql = generateSql(
                "MySQL",
                "Get all students who has at least one class where their grade is above average"
        );

        if (sql.code != null) {
            System.out.println("--- CODE -----------------------");
            System.out.println(sql.code);
            System.out.println("--- COMMENT --------------------");
            System.out.println(sql.comment);
            System.out.println("--------------------------------");
        } else {
            System.out.println("--------------------------------");
            System.out.println(sql.comment);
            System.out.println("--------------------------------");
        }
    }

    private static SqlCode generateSql(String dialect, String prompt) throws IOException {
        String apiToken = Files.readString(Path.of("P:\\oapi.txt"));
        OpenAiService service = new OpenAiService(apiToken);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(List.of(
                        new ChatMessage("system",
                                "You are a helpful assistant who produces " + dialect + " code."
                        ),
                        new ChatMessage("user", prompt)
                ))
                .build();
        String response = service.createChatCompletion(request).getChoices().get(0).getMessage().getContent();

        int start = response.indexOf("```");
        if (start != -1) {
            start += 3;
            int end = response.indexOf("```", start);
            if (end != -1) {
                String code = response.substring(start, end).trim();
                String comment = response.substring(end + 3).trim();
                return new SqlCode(code, comment);
            }

        }

        return new SqlCode(null, response);
    }

    private static final class SqlCode {
        public final String code;
        public final String comment;

        public SqlCode(String code, String comment) {
            this.code = code;
            this.comment = comment;
        }
    }
}
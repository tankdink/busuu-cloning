package com.busuu.app.services.client;

import com.busuu.app.client.OpenAiFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiTranslationService {
    @Value("${services.openai.api-key}")
    private String apiKey;

    @Value("${services.openai.model}")
    private String model;

    private final OpenAiFeignClient openAiFeignClient;

    /**
     * Translate a single word from sourceLang to targetLang
     */
    public String translateWord(String word, String sourceLang, String targetLang) {
        String prompt = String.format(
                "Translate the single word \"%s\" from %s to %s. " +
                        "Provide only the translated word without any additional explanation.",
                word, sourceLang, targetLang);

        return callOpenAiApi("You are a helpful translator.", prompt);
    }

    /**
     * Translate a full sentence from sourceLang to targetLang
     */
    public String translateSentence(String sentence, String sourceLang, String targetLang) {
        String prompt = String.format(
                "Translate the following sentence from %s to %s accurately and naturally:\n\n\"%s\"",
                sourceLang, targetLang, sentence);

        return callOpenAiApi("You are a helpful translator.", prompt);
    }

    /**
     * Generic OpenAI API call that can be reused for different tasks
     */
    public String callOpenAiApi(String systemRole, String userPrompt) {
        String bearerToken = "Bearer " + apiKey;

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemRole),
                        Map.of("role", "user", "content", userPrompt)
                )
        );

        Map<String, Object> response = openAiFeignClient.createChatCompletion(bearerToken, requestBody);

        // Parse choices[0].message.content
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices != null && !choices.isEmpty()) {
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            if (message != null) {
                return (String) message.get("content");
            }
        }
        return null;
    }
}

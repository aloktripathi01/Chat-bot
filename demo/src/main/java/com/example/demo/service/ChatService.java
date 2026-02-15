package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.entity.ChatMessage;
import com.example.demo.entity.User;
import com.example.demo.reop.ChatMessageRepository;

import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Service
public class ChatService {

    @Value("${google.api.key}")
    private String apiKey;

    @Value("${ai.model}")
    private String model;

    @Value("${ai.system-prompt}")
    private String systemPrompt;

    private final WebClient webClient;
    private final ChatMessageRepository repository;


    public ChatService(WebClient.Builder builder,
            ChatMessageRepository repository) {

this.repository = repository;

this.webClient = builder
     .baseUrl("https://generativelanguage.googleapis.com/v1")
     .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
     .build();
}

    public String getResponse(User user, String message) {

        // Save USER message
        ChatMessage userMessage = new ChatMessage();
        userMessage.setUser(user);
        userMessage.setRole("user");
        userMessage.setContent(message);
        userMessage.setTimestamp(LocalDateTime.now());
        repository.save(userMessage);

        try {

            Map<String, Object> textPart = Map.of("text", message);
            Map<String, Object> content = Map.of("parts", List.of(textPart));
            Map<String, Object> request = Map.of(
                    "contents", List.of(content)
            );

            String response = webClient.post()
                    .uri("/models/gemini-2.5-flash:generateContent?key=" + apiKey)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .map(res -> res.get("candidates")
                            .get(0)
                            .get("content")
                            .get("parts")
                            .get(0)
                            .get("text")
                            .asText())
                    .block();

            // Save BOT message
            ChatMessage botMessage = new ChatMessage();
            botMessage.setUser(user);
            botMessage.setRole("bot");
            botMessage.setContent(response);
            botMessage.setTimestamp(LocalDateTime.now());
            repository.save(botMessage);

            return response;

        } catch (Exception e) {

            String errorMessage = "⚠️ AI is busy. Try again later.";

            ChatMessage botMessage = new ChatMessage();
            botMessage.setUser(user);
            botMessage.setRole("bot");
            botMessage.setContent(errorMessage);
            botMessage.setTimestamp(LocalDateTime.now());
            repository.save(botMessage);

            return errorMessage;
        }
    }



}

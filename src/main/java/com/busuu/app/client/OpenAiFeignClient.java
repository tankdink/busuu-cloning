package com.busuu.app.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "openaiClient", url = "${services.openai.endpoint}")
public interface OpenAiFeignClient {

    @PostMapping("/chat/completions")
    Map<String, Object> createChatCompletion(
            @RequestHeader("Authorization") String authorization,
            @RequestBody Map<String, Object> requestBody
    );
}
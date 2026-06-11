package com.ram.loangpt.service;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class ResourceService {

    private String systemPrompt;
    private String jsonSchema;

    @PostConstruct
    void init() throws IOException {
        ClassPathResource prompt =
                new ClassPathResource("prompts/loanSystemPrompt.txt");

        systemPrompt = new String(
                prompt.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        ClassPathResource resource =
                new ClassPathResource("schemas/loanRequestSchema.json");

        jsonSchema = new String(
                resource.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }
    public String getJsonSchema() {
        return jsonSchema;
    }
}
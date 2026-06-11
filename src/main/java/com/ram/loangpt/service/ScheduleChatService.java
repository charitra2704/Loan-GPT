package com.ram.loangpt.service;

import com.ram.loangpt.dto.LoanRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ram.loangpt.enums.LlmType;
import com.ram.loangpt.service.Client.LLMClient;
import com.ram.loangpt.service.Client.LLMClientFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;



/**
 * @author chari
 **/

@Service
public class ScheduleChatService {
    private final ObjectMapper objectMapper;
    private final LLMClientFactory llmClientFactory;

    public ScheduleChatService(LLMClientFactory llmClientFactory) {
        this.objectMapper = new ObjectMapper();
        this.llmClientFactory = llmClientFactory;
    }

public LoanRequest generateResponse(String userInput) throws Exception {

    // Use any LLM of your choice
    LLMClient llmClient = llmClientFactory.getLlmClient(LlmType.GEMINI);

    String jsonResponse = llmClient.generateJson(userInput);

    return objectMapper.readValue(jsonResponse,LoanRequest.class);
}
}

package com.ram.loangpt.service.Client;

import com.ram.loangpt.enums.LlmType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author chari
 **/
@Component
public class LLMClientFactory {
    @Autowired
    private GeminiLlmClient geminiLlmClient;

    public LLMClient getLlmClient(LlmType llmType){
        switch (llmType){
            case LlmType.GEMINI -> {
                return geminiLlmClient;
            }
            default -> {
                throw new IllegalArgumentException("Invalid LLM: " + llmType);
            }
        }
    }
}

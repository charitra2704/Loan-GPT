package com.ram.loangpt.service.Client;

import org.springframework.stereotype.Component;

/**
 * @author chari
 **/
@Component
public interface LLMClient {
    String generateJson(String userPrompt);
}

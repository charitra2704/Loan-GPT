package com.ram.loangpt.service.Client;

import autovalue.shaded.com.google.common.collect.ImmutableList;
import com.google.genai.Client;
import com.google.genai.ResponseStream;
import com.google.genai.types.*;
import com.ram.loangpt.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chari
 **/

@Service
public class GeminiLlmClient implements LLMClient {

    @Value("${gemini.api.key}")
    private String apikey;

    @Value("${gemini.model}")
    private String model;

    @Autowired private ResourceService resourceService;

    public String generateJson(String userPrompt){

        StringBuilder jsonResponse = new StringBuilder();
        Client client = Client.builder().apiKey(apikey).build();
        List<Tool> tools = new ArrayList<>();
            tools.add(
            Tool.builder()
                    .googleSearch(
                            GoogleSearch.builder().build()
                    )
                    .build());
            List<Content> contents = ImmutableList.of(
            Content.builder()
                    .role("user")
                    .parts(ImmutableList.of(
                            Part.fromText(userPrompt)
                    ))
                    .build());
            GenerateContentConfig config =
            GenerateContentConfig
                    .builder()
                    .tools(tools)
                    .responseMimeType("application/json")
                    .responseSchema(Schema.fromJson(resourceService.getJsonSchema()))
                    .systemInstruction(
                            Content
                                    .fromParts(
                                            Part.fromText(resourceService.getSystemPrompt())
                                    )
                    )
                    .build();
            ResponseStream<GenerateContentResponse> responseStream = client.models.generateContentStream(model, contents, config);

            for (GenerateContentResponse res : responseStream) {
                if (res.candidates().isEmpty() ||
                        res.candidates().get().get(0).content().isEmpty() ||
                        res.candidates().get().get(0).content().get().parts().isEmpty()) {
                    continue;
                }

        List<Part> parts = res.candidates().get().get(0).content().get().parts().get();
        for (Part part : parts) {
            if (part.text().isPresent()) {
                jsonResponse.append(part.text().get());
            }
        }
    }
    responseStream.close();

    return jsonResponse.toString();
    }
}

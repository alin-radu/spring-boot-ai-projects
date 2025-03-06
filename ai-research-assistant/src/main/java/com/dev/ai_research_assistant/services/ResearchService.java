package com.dev.ai_research_assistant.services;

import com.dev.ai_research_assistant.domain.dtos.GeminiAiResponse;
import com.dev.ai_research_assistant.domain.dtos.ResearchRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class ResearchService {
    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public ResearchService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public String processContent(ResearchRequestDto researchRequestDto) {

        String prompt = buildPrompt(researchRequestDto);

        Map<String, Object> requestBody = getRequestBody(prompt);
        String response = webClient.post()
                .uri(geminiApiUrl + geminiApiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return extractTextFromResponse(response);
    }

    // buildPrompt
    private String buildPrompt(ResearchRequestDto request) {
        StringBuilder prompt = new StringBuilder();
        switch (request.getOperation()) {
            case "summarize":
                prompt.append("Provide a clear and concise summary of the following text in a few sentences:\n\n");
                break;
            case "suggest":
                prompt.append("Based on the following content: suggest related topics and further reading. Format the response with clear headings and bullet points:\n\n");
                break;
            default:
                throw new IllegalArgumentException("Unknown Operation: " + request.getOperation());
        }
        prompt.append(request.getContent());

        return prompt.toString();
    }

    // getRequestBody
    private Map<String, Object> getRequestBody(String prompt) {
        return Map.of(
                "contents", new Object[]{
                        Map.of(
                                "parts", new Object[]{
                                        Map.of(
                                                "text", prompt
                                        )}
                        )}
        );
    }

    // extractTextFromResponse
    private String extractTextFromResponse(String response) {
        try {
            GeminiAiResponse geminiAiResponse = objectMapper.readValue(response, GeminiAiResponse.class);

            if (geminiAiResponse.getCandidates() != null && !geminiAiResponse.getCandidates().isEmpty()) {
                GeminiAiResponse.Candidate firstCandidate = geminiAiResponse.getCandidates().getFirst();
                if (firstCandidate.getContent() != null &&
                        firstCandidate.getContent().getParts() != null &&
                        !firstCandidate.getContent().getParts().isEmpty()) {
                    return firstCandidate.getContent().getParts().getFirst().getText();
                }
            }

            return "No content found in the Gemini AI API";
        } catch (Exception ex) {
            return "Error Parsing: " + ex.getMessage();
        }
    }
}

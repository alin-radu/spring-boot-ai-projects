package com.dev.ai_research_assistant.controllers;

import com.dev.ai_research_assistant.domain.dtos.ResearchRequestDto;
import com.dev.ai_research_assistant.services.ResearchService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/research")
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ResearchController {
    private final ResearchService researchService;

    public ResponseEntity<String> processContent(@RequestBody ResearchRequestDto researchRequestDto) {

        String result = researchService.processContent(researchRequestDto);
        return ResponseEntity.ok(result);
    }
}

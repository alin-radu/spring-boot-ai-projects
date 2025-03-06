package com.dev.ai_research_assistant.controllers;

import com.dev.ai_research_assistant.domain.dtos.ResearchRequestDto;
import com.dev.ai_research_assistant.services.ResearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/research")
@RequiredArgsConstructor
public class ResearchController {
    private final ResearchService researchService;

    @PostMapping("/process")
    public ResponseEntity<String> processContent(@RequestBody ResearchRequestDto researchRequestDto) {

        String result = researchService.processContent(researchRequestDto);
        return ResponseEntity.ok(result);
    }
}

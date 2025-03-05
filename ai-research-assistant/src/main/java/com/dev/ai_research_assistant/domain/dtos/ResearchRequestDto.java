package com.dev.ai_research_assistant.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResearchRequestDto {
    private String content;
    private String operation;
}

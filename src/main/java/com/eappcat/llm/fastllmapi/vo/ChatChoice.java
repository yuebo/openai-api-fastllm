package com.eappcat.llm.fastllmapi.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChatChoice {
    private int index;
    private ChatMessage message;
    private ChatMessage delta;
    @JsonProperty("finish_reason")
    private String finishReason;
}

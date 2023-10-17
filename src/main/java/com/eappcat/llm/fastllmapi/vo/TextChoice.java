package com.eappcat.llm.fastllmapi.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TextChoice {
    private String text;

    @JsonProperty("finish_reason")
    private String finishReason;
    private String logprobs;
    private int index;
}

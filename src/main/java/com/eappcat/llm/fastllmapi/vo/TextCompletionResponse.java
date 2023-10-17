package com.eappcat.llm.fastllmapi.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TextCompletionResponse {
    private String id;
    private String object="text_completion";

    private long created = System.currentTimeMillis()/1000;

    private String model;

    private Usage usage;

    private List<TextChoice> choices=new ArrayList<>();
}

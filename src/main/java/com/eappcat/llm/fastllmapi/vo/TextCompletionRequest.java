package com.eappcat.llm.fastllmapi.vo;

import lombok.Data;

@Data
public class TextCompletionRequest {
    private String prompt;
    private String model="gpt-3.5-turbo";
    private boolean stream;
    private String user="";
}

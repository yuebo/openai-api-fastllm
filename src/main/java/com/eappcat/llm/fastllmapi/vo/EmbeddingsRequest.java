package com.eappcat.llm.fastllmapi.vo;

import lombok.Data;

import java.util.List;


@Data
public class EmbeddingsRequest {
    private List<String> input;
    private String model;
    private String user;
}

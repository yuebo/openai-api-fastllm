package com.eappcat.llm.fastllmapi.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Embeddings {
    private String object="embedding";
    private List<BigDecimal> embedding;
    private int index;
}

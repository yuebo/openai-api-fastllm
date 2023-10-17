package com.eappcat.llm.fastllmapi.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EmbeddingsResponse {
    private String object="list";

    private long created = System.currentTimeMillis()/1000;

    private String model="text-embedding-ada-002";

    private Usage usage;

    private List<Embeddings> data =new ArrayList<>();
}

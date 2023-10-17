package com.eappcat.llm.fastllmapi.vo;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatResponse {

    private String id;
    private String object="chat.completion";

    private long created = System.currentTimeMillis()/1000;

    private String model;

    private Usage usage;

    private List<ChatChoice> choices =new ArrayList<>();
}

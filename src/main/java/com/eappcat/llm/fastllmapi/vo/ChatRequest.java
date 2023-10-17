package com.eappcat.llm.fastllmapi.vo;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatRequest {

    private String user="";
    private String model="chatglm";
    private boolean stream;
    private List<ChatMessage> messages=new ArrayList<>();
}

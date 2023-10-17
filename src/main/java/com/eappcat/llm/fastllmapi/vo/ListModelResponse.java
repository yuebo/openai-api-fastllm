package com.eappcat.llm.fastllmapi.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ListModelResponse {
    private String object = "list";
    private List<Model> data=new ArrayList<>();
}

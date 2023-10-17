package com.eappcat.llm.fastllmapi.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Model {
    private String id;
    private String object="model";
    private long created=System.currentTimeMillis()/1000;
    @JsonProperty("owned_by")
    private String ownedBy;

    public static Model of(String id){
        Model model = new Model();
        model.setId(id);
        model.setOwnedBy("openai");
        return model;
    }
}

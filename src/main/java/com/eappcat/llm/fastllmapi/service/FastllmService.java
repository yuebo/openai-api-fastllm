package com.eappcat.llm.fastllmapi.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.eappcat.llm.fastllmapi.props.FastllmConfigProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FastllmService {

    private Interner<String> interner = Interners.<String>newWeakInterner();


    @Autowired
    private FastllmConfigProperties fastllmConfigProperties;

    public boolean clearHistory(String uid){
        boolean isRunning = true;
        while (isRunning){
            try(HttpResponse res = HttpRequest.post(fastllmConfigProperties.getUrl().concat("/chat"))
                    .body("reset")
                    .header("uuid",uid)
                    .execute()) {
                if (StringUtils.endsWith(res.body(),"<eop>\n")){
                    isRunning=false;
                }
            }
        }
        return true;
    }

    public String intern(String value){
        return interner.intern(String.valueOf(value));
    }

}

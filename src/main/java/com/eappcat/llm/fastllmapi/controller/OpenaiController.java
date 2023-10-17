package com.eappcat.llm.fastllmapi.controller;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;
import com.eappcat.llm.fastllmapi.event.QwenSseEvent;
import com.eappcat.llm.fastllmapi.props.FastllmConfigProperties;
import com.eappcat.llm.fastllmapi.service.FastllmService;
import com.eappcat.llm.fastllmapi.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Arrays;

@RestController
@RequestMapping("/openai-api/v1")
public class OpenaiController {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private FastllmConfigProperties fastllmConfigProperties;

    @Autowired
    private FastllmService fastllmService;


    @GetMapping("/models")
    public ListModelResponse listModels(){
        ListModelResponse response = new ListModelResponse();
        response.setData(Arrays.asList(
            Model.of("gpt-3.5-turbo"),
            Model.of("gpt-3.5-turbo-instruct")
        ));

        return response;

    }

    @GetMapping("/models/{model}")
    public Model model(@PathVariable("model") String model){
        return Model.of(model);
    }

    @PostMapping("/chat/completions")
    public Object chatComplete(@RequestBody  ChatRequest request) throws Exception{
        if (request.isStream()){
            SseEmitter emitter = new SseEmitter();
            applicationContext.publishEvent(new QwenSseEvent(emitter,request));
            return emitter;
        }else {
            synchronized (fastllmService.intern(request.getUser())) {
                String message = "";
                try (HttpResponse res = HttpRequest.post(fastllmConfigProperties.getQwenUrl().concat("/v1/chat/completions"))
                        .body(JSONObject.toJSONString(request))
                        .execute()) {
                    message = res.body();
                }
                return JSONObject.parseObject(message,ChatResponse.class);
            }
        }

    }

}

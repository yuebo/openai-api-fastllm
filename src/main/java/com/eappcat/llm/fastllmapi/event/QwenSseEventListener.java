package com.eappcat.llm.fastllmapi.event;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;
import com.eappcat.llm.fastllmapi.props.FastllmConfigProperties;
import com.eappcat.llm.fastllmapi.service.FastllmService;
import com.eappcat.llm.fastllmapi.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Arrays;
import java.util.UUID;

@Component
@Slf4j
public class QwenSseEventListener {

    @Autowired
    private FastllmConfigProperties fastllmConfigProperties;

    @Autowired
    private FastllmService fastllmService;

    @EventListener
    @Async
    public void onSseEvent(QwenSseEvent event){
        doChatStream((ChatRequest) event.getRequest(),event);
    }
    void doChatStream(ChatRequest request,QwenSseEvent event){
        synchronized (fastllmService.intern(request.getUser())){
            SseEmitter emitter = event.getEmitter();
            try {
                String message = "";
                request.setStream(false);
                try(HttpResponse response = HttpRequest.post(fastllmConfigProperties.getQwenUrl().concat("/v1/chat/completions"))
                        .body(JSONObject.toJSONString(request)).header("Content-Type","application/json")
                        .execute()) {
                    if (response.isOk()){
                        message = response.body();
                    }else {
                        emitter.send("{ \"error\":\"failed to request api\"}");
                        return;
                    }
                }

                ChatResponse source = JSONObject.parseObject(message).to(ChatResponse.class);

                ChatResponse response = new ChatResponse();
                response.setId("chatcmpl-"+UUID.randomUUID().toString());
                response.setModel(request.getModel());
                response.setObject("chat.completion.chunk");
                response.setUsage(new Usage());

                ChatChoice chatChoice = new ChatChoice();
                chatChoice.setDelta(new ChatMessage("assistant",source.getChoices().get(0).getMessage().getContent()));
                chatChoice.setFinishReason("stop");
                chatChoice.setIndex(0);
                response.setChoices(Arrays.asList(chatChoice));
                emitter.send(" "+JSONObject.toJSONString(response));
                emitter.send(" [DONE]");
            }catch (Exception e){
                log.warn("error to process request", e);
            }finally {
                emitter.complete();
            }
        }
    }
}

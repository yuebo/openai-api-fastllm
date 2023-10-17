package com.eappcat.llm.fastllmapi.event;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;
import com.eappcat.llm.fastllmapi.props.FastllmConfigProperties;
import com.eappcat.llm.fastllmapi.service.FastllmService;
import com.eappcat.llm.fastllmapi.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Arrays;
import java.util.UUID;

@Component
@Slf4j
public class SseEventListener {

    @Autowired
    private FastllmConfigProperties fastllmConfigProperties;

    @Autowired
    private FastllmService fastllmService;

    @EventListener
    @Async
    public void onSseEvent(SseEvent event){

        if (event.getRequest() instanceof ChatRequest){
            doChatStream((ChatRequest)event.getRequest(),event);
        }else if (event.getRequest() instanceof TextCompletionRequest){
            doTextStream((TextCompletionRequest) event.getRequest(),event);
        }
    }
    void doChatStream(ChatRequest request,SseEvent event){
        synchronized (fastllmService.intern(request.getUser())){
            SseEmitter emitter = event.getEmitter();
            StringBuilder stringBuilder = new StringBuilder();
            for (ChatMessage message:request.getMessages()){
                stringBuilder.append(message.getContent()).append("\n");
            }
            try {
                boolean isRunning = true;
                int len = 0;
                while (isRunning){
                    Thread.sleep(300);
                    String message = "";
                    try(HttpResponse response = HttpRequest.post(fastllmConfigProperties.getUrl().concat("/chat"))
                            .body(stringBuilder.toString())
                            .header("uuid",request.getUser())
                            .execute()) {
                        if (response.isOk()){
                            message = response.body();
                        }else {
                            emitter.send("{ \"error\":\"failed to request api\"}");
                        }
                    }

                    log.info("{}",message);

                    if (StringUtils.endsWith(message,"<eop>\n")){
                        message=StringUtils.substring(message,0,-6);
                        isRunning=false;
                    }
                    if (StringUtils.isEmpty(message)){
                        continue;
                    }

                    message = message.substring(len);
                    len = message.length();

                    ChatResponse response = new ChatResponse();
                    response.setId("chatcmpl-"+UUID.randomUUID().toString());
                    response.setModel(request.getModel());
                    response.setObject("chat.completion.chunk");
                    response.setUsage(new Usage());

                    ChatChoice chatChoice = new ChatChoice();
                    chatChoice.setDelta(new ChatMessage("assistant",message));
                    chatChoice.setFinishReason(isRunning?null:"stop");
                    chatChoice.setIndex(0);
                    response.setChoices(Arrays.asList(chatChoice));
                    emitter.send(" "+JSONObject.toJSONString(response));
                    if (!isRunning){
                        emitter.send(" [DONE]");
                    }

                }
            }catch (Exception e){
                log.warn("error to process request", e);
            }finally {
                fastllmService.clearHistory(request.getUser());
                emitter.complete();
            }
        }
    }


    void doTextStream(TextCompletionRequest request,SseEvent event){
        synchronized (fastllmService.intern(request.getUser())) {
            SseEmitter emitter = event.getEmitter();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(request.getPrompt());
            try {
                boolean isRunning = true;
                int len = 0;
                while (isRunning) {
                    Thread.sleep(300);
                    String message = "";
                    try (HttpResponse response = HttpRequest.post(fastllmConfigProperties.getUrl().concat("/chat"))
                            .body(stringBuilder.toString())
                            .header("uuid", request.getUser())
                            .execute()) {
                        if (response.isOk()) {
                            message = response.body();
                        } else {
                            emitter.send("{ \"error\":\"failed to request api\"}");
                        }

                    }

                    log.info("{}", message);

                    if (StringUtils.endsWith(message, "<eop>\n")) {
                        message = StringUtils.substring(message, 0, -6);
                        isRunning = false;
                    }
                    if (StringUtils.isEmpty(message)) {
                        continue;
                    }

                    message = message.substring(len);
                    len = message.length();

                    TextCompletionResponse response = new TextCompletionResponse();
                    response.setId("cmpl-" + UUID.randomUUID().toString());
                    response.setModel(request.getModel());
                    response.setUsage(new Usage());

                    TextChoice choice = new TextChoice();
                    choice.setText(message);
                    choice.setFinishReason(isRunning ? null : "stop");
                    choice.setIndex(0);
                    response.setChoices(Arrays.asList(choice));
                    emitter.send(" " + JSONObject.toJSONString(response));
                    if (!isRunning) {
                        emitter.send(" [DONE]");
                    }

                }
            } catch (Exception e) {
                log.warn("error to process request", e);
            } finally {
                fastllmService.clearHistory(request.getUser());
                emitter.complete();
            }
        }
    }
}

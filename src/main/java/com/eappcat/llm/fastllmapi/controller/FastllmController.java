package com.eappcat.llm.fastllmapi.controller;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;
import com.eappcat.llm.fastllmapi.event.SseEvent;
import com.eappcat.llm.fastllmapi.props.FastllmConfigProperties;
import com.eappcat.llm.fastllmapi.service.FastllmService;
import com.eappcat.llm.fastllmapi.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/fastllm-api/v1")
public class FastllmController {

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
            Model.of("gpt-3.5-turbo-0613"),
            Model.of("gpt-3.5-turbo-instruct"),
            Model.of("text-embedding-ada-002")
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
            applicationContext.publishEvent(new SseEvent(emitter,request));
            return emitter;
        }else {
            ChatResponse response = new ChatResponse();
            synchronized (fastllmService.intern(request.getUser())) {
                StringBuilder stringBuilder = new StringBuilder();
                for (ChatMessage message : request.getMessages()) {
                    stringBuilder.append(message.getContent()).append("\n");
                }
                boolean isRunning = true;
                while (isRunning) {
                    Thread.sleep(200);
                    String message = "";
                    try (HttpResponse res = HttpRequest.post(fastllmConfigProperties.getUrl().concat("/chat"))
                            .body(stringBuilder.toString())
                            .header("uuid", request.getUser())
                            .execute()) {
                        message = res.body();
                    }
                    if (StringUtils.endsWith(message, "<eop>\n")) {
                        message = StringUtils.substring(message, 0, -6);
                        isRunning = false;

                        response.setId(UUID.randomUUID().toString());
                        response.setModel(request.getModel());
                        response.setUsage(new Usage());
                        ChatChoice chatChoice = new ChatChoice();
                        chatChoice.setMessage(new ChatMessage("assistant", message));
                        chatChoice.setFinishReason("stop");
                        chatChoice.setIndex(0);
                        response.setChoices(Arrays.asList(chatChoice));
                    }

                }
                fastllmService.clearHistory(request.getUser());
            }
            return response;
        }

    }

    @PostMapping("/completions")
    public Object completion(@RequestBody TextCompletionRequest request) throws Exception{
        if (request.isStream()){
            SseEmitter emitter = new SseEmitter();
            applicationContext.publishEvent(new SseEvent(emitter,request));
            return emitter;
        }else {
            TextCompletionResponse response = new TextCompletionResponse();

            synchronized (fastllmService.intern(request.getUser())){
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(request.getPrompt());
                boolean isRunning = true;
                while (isRunning) {
                    Thread.sleep(200);
                    String message = "";
                    try (HttpResponse res = HttpRequest.post(fastllmConfigProperties.getUrl().concat("/chat"))
                            .body(stringBuilder.toString())
                            .header("uuid", request.getUser())
                            .execute()) {
                        message = res.body();
                    }
                    if (StringUtils.endsWith(message, "<eop>\n")) {
                        message = StringUtils.substring(message, 0, -6);
                        isRunning = false;
                        response.setId(UUID.randomUUID().toString());
                        response.setModel(request.getModel());
                        response.setUsage(new Usage());
                        TextChoice choice = new TextChoice();
                        choice.setText(message);
                        choice.setFinishReason("stop");
                        choice.setIndex(0);
                        response.setChoices(Arrays.asList(choice));
                    }
                }
                fastllmService.clearHistory(request.getUser());
            }

            return response;
        }
    }

    @PostMapping("/embeddings")
    public Object embeddings(@RequestBody String body){

        EmbeddingsRequest request = new EmbeddingsRequest();
        JSONObject jsonObject = JSONObject.parseObject(body);
        Object input = jsonObject.get("input");
        if (input == null){
            request.setInput(new ArrayList<>());
        }else if (input instanceof String){
            request.setInput(Arrays.asList((String)input));
        }else {
            request.setInput(jsonObject.getJSONArray("input").toList(String.class));
        }
        request.setModel(jsonObject.getString("model"));
        request.setUser(jsonObject.getString("user"));

        EmbeddingsResponse response =new EmbeddingsResponse();

        List<Embeddings> embeddingsList = new ArrayList<>();


        JSONObject sentences =new JSONObject();
        sentences.put("sentences",request.getInput());
        try (HttpResponse res = HttpRequest.post(fastllmConfigProperties.getEmbeddingUrl().concat("/embedding"))
                .body(sentences.toJSONString())
                .execute()) {
            JSONObject sentencesRes = JSONObject.parseObject(res.body());

            for (int i = 0; i < request.getInput().size(); i++) {
                Embeddings embeddings = new Embeddings();
                embeddings.setEmbedding(sentencesRes.getJSONArray("embeddings").getJSONArray(0).toJavaList(BigDecimal.class));
                embeddings.setIndex(i);
                embeddingsList.add(embeddings);
            }
        }
        response.setData(embeddingsList);
        response.setUsage(new Usage());
        return response;

    }

}

package com.eappcat.llm.fastllmapi.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QwenSseEvent {
    private SseEmitter emitter;
    private Object request;
}

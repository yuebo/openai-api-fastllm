package com.eappcat.llm.fastllmapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FastllmApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FastllmApiApplication.class, args);
    }

}

package com.autocat.playground.feign_with_decoder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class FeignWithDecoderApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeignWithDecoderApplication.class, args);
    }
}

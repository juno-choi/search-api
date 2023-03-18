package com.juno.search.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Configuration
@RequiredArgsConstructor
public class SearchClient {
    private final Environment env;
    // kakao
    @Bean
    public WebClient kakaoSearch(){
        return WebClient.builder().baseUrl(env.getProperty("api.kakao.url"))
                .defaultHeader(AUTHORIZATION, "KakaoAK "+ env.getProperty("api.kakao.key"))
                .build();
    }

    // naver
    @Bean
    public WebClient naverSearch(){
        return WebClient.builder().baseUrl("")
                .defaultHeader("")
                .build();
    }
}

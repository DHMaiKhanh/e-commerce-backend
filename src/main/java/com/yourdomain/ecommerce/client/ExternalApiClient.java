package com.yourdomain.ecommerce.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
public class ExternalApiClient {

    private final WebClient webClient;

    public ExternalApiClient(WebClient.Builder builder,
                             @Value("${app.integration.external-api.base-url:https://api.example.com}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    public <T> Mono<T> get(String path, Class<T> responseType) {
        return webClient.get()
                .uri(path)
                .retrieve()
                .bodyToMono(responseType)
                .timeout(Duration.ofSeconds(10))
                .doOnError(WebClientResponseException.class,
                        ex -> log.error("External API {} failed: status={} body={}",
                                path, ex.getStatusCode(), ex.getResponseBodyAsString()));
    }
}

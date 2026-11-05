package com.edu.unimag.Gestion_de_activos__Modulo_1.infrastructure.external.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class ExternalModulesConfig {

    @Value("${external.modulo2.base-url:http://localhost:8082}")
    private String modulo2BaseUrl;

    @Value("${external.modulo2.connect-timeout-ms:2000}")
    private int modulo2ConnectTimeoutMs;

    @Value("${external.modulo2.read-timeout-ms:3000}")
    private int modulo2ReadTimeoutMs;

    @Value("${external.modulo3.base-url:http://localhost:8083}")
    private String modulo3BaseUrl;

    @Value("${external.modulo3.connect-timeout-ms:2000}")
    private int modulo3ConnectTimeoutMs;

    @Value("${external.modulo3.read-timeout-ms:3000}")
    private int modulo3ReadTimeoutMs;

    @Bean
    public RestClient modulo2RestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(modulo2ConnectTimeoutMs));
        requestFactory.setReadTimeout(Duration.ofMillis(modulo2ReadTimeoutMs));

        return RestClient.builder()
                .baseUrl(modulo2BaseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @Bean
    public RestClient modulo3RestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(modulo3ConnectTimeoutMs));
        requestFactory.setReadTimeout(Duration.ofMillis(modulo3ReadTimeoutMs));

        return RestClient.builder()
                .baseUrl(modulo3BaseUrl)
                .requestFactory(requestFactory)
                .build();
    }
}


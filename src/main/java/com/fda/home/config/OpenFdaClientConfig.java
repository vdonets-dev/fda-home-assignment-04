package com.fda.home.config;

import com.fda.home.exception.OpenFdaApiException;
import com.fda.home.exception.OpenFdaBadRequestException;
import com.fda.home.exception.OpenFdaNotFoundException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class OpenFdaClientConfig {

    @Value("${openfda.api.connect-timeout}")
    private int connectTimeout;

    @Value("${openfda.api.read-timeout}")
    private int readTimeout;

    @Value("${openfda.api.retry.max-attempts}")
    private int maxAttempts;

    @Value("${openfda.api.retry.backoff-interval}")
    private long backoffInterval;

    @Value("${openfda.api.circuit-breaker.name}")
    private String circuitBreakerName;

    @Bean
    @ConditionalOnMissingBean(name = "openFdaRestTemplate")
    public RestTemplate openFdaRestTemplate() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        log.info("RestTemplate timeouts: connect={}, read={}", connectTimeout, readTimeout);
        return new RestTemplate(factory);
    }

    @Bean
    public RetryTemplate retryTemplate() {
        return RetryTemplate.builder()
                .maxAttempts(maxAttempts)
                .exponentialBackoff(backoffInterval, 2, 10000)
                .retryOn(HttpClientErrorException.class)
                .build();
    }

    @Bean
    public CircuitBreaker circuitBreaker(CircuitBreakerRegistry registry) {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .ignoreExceptions(OpenFdaApiException.class,
                        OpenFdaBadRequestException.class,
                        OpenFdaNotFoundException.class)
                .build();

        return registry.circuitBreaker(circuitBreakerName, config);
    }
}
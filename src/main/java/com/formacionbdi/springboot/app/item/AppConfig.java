package com.formacionbdi.springboot.app.item;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;


@Configuration
public class AppConfig {

    @Bean("clienteRest")
    public RestTemplate registrarRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> {
            return new Resilience4JConfigBuilder(id)
                    .circuitBreakerConfig(CircuitBreakerConfig.custom()
                            //cantidad de request q se toman en cuenta para analizar el corto circuito (100 por defecto)
                            .slidingWindowSize(10)
                            // tasa de falla para q se ejecute el corto circuito (50% por defecto)
                            .failureRateThreshold(50)
                            // duracion del estado abierto del corto circuito (60s por defecto)
                            .waitDurationInOpenState(Duration.ofSeconds(10L))
                            //numero de llamadas permitidas en el estado semiabierto (10 por defecto)
                            .permittedNumberOfCallsInHalfOpenState(5)
                            // umbral en % de las llamadas lentas permitidas (100% por defecto)
                            .slowCallRateThreshold(50)
                            // duracion de lo q se considera una llamada lenta
                            .slowCallDurationThreshold(Duration.ofSeconds(2L))
                            .build())
                    .timeLimiterConfig(TimeLimiterConfig.custom()
                            // duracion del tiempo de espera permitido de las llamadas (1s por defecto)
                            .timeoutDuration(Duration.ofSeconds(6L))
                            .build())
                    .build();
        });
    }
}

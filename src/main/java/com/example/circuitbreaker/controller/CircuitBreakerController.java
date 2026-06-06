package com.example.circuitbreaker.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@RestController
@RequestMapping("/api/circuit-breaker")
public class CircuitBreakerController {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public CircuitBreakerController(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @GetMapping(path = "/state", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getState() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("userService");
        return ResponseEntity.ok(circuitBreaker.getState().name());
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("userService");
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("name", circuitBreaker.getName());
        response.put("state", circuitBreaker.getState().name());
        response.put("failureRate", metrics.getFailureRate());
        response.put("bufferedCalls", metrics.getNumberOfBufferedCalls());
        response.put("failedCalls", metrics.getNumberOfFailedCalls());
        response.put("successfulCalls", metrics.getNumberOfSuccessfulCalls());
        response.put("notPermittedCalls", metrics.getNumberOfNotPermittedCalls());

        return ResponseEntity.ok(response);
    }
}
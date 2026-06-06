package com.example.circuitbreaker.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@Component
public class CircuitBreakerLoggingConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(CircuitBreakerLoggingConfiguration.class);

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public CircuitBreakerLoggingConfiguration(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registerCircuitBreakerListeners() {
        circuitBreakerRegistry.circuitBreaker("userService")
                .getEventPublisher()
                .onStateTransition(event -> logger.info("CircuitBreaker '{}' changed state from {} to {}",
                        event.getCircuitBreakerName(),
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState()));
    }
}
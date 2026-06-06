package com.example.circuitbreaker.service;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Component;

@Component
public class MockExternalBackendState {

    public enum Mode {
        HEALTHY,
        FAILING;

        public static Mode fromValue(String value) {
            String normalizedValue = value.toLowerCase(Locale.ROOT);
            return switch (normalizedValue) {
                case "healthy", "up", "ok" -> HEALTHY;
                case "fail", "failing", "down" -> FAILING;
                default -> throw new IllegalArgumentException("Unsupported backend mode: " + value);
            };
        }
    }

    private final AtomicReference<Mode> mode = new AtomicReference<>(Mode.HEALTHY);

    public Mode getMode() {
        return mode.get();
    }

    public void setMode(Mode mode) {
        this.mode.set(mode);
    }
}
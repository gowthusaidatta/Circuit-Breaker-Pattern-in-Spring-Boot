package com.example.circuitbreaker.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {
                "server.port=18080",
                "app.external.base-url=http://localhost:18080/mock/external"
        })
public class CircuitBreakerIntegrationTest {

    private static final String BASE_URL = "http://localhost:18080";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void circuitBreakerOpensAndCloses() {
        // 1) Set backend to failing
        restTemplate.postForEntity(BASE_URL + "/mock/external/mode/fail", null, String.class);

        // 2) Trigger failures - enough to open the circuit (minimum-number-of-calls=5)
        for (int i = 0; i < 6; i++) {
            try {
                restTemplate.getForEntity(BASE_URL + "/api/users/1", String.class);
            } catch (Exception e) {
                // expected while backend failing
            }
        }

        // 3) Await OPEN
        Awaitility.await().atMost(Duration.ofSeconds(5)).pollInterval(Duration.ofMillis(200))
                .untilAsserted(() -> {
                    ResponseEntity<String> s = restTemplate.getForEntity(BASE_URL + "/api/circuit-breaker/state", String.class);
                    assertEquals("OPEN", s.getBody());
                });

        // 4) Wait for wait-duration-in-open-state (10s) then set healthy
        Awaitility.await()
                .pollDelay(Duration.ofSeconds(11))
                .atMost(Duration.ofSeconds(12))
                .until(() -> true);
        restTemplate.postForEntity(BASE_URL + "/mock/external/mode/healthy", null, String.class);

        // 5) Probe success and finally await CLOSED
        Awaitility.await().atMost(Duration.ofSeconds(30)).pollInterval(Duration.ofSeconds(1)).untilAsserted(() -> {
            restTemplate.getForEntity(BASE_URL + "/api/users/1", String.class);
            ResponseEntity<String> s = restTemplate.getForEntity(BASE_URL + "/api/circuit-breaker/state", String.class);
            assertEquals("CLOSED", s.getBody());
        });
    }
}

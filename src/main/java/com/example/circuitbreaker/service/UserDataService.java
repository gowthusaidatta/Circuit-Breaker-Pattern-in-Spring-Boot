package com.example.circuitbreaker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.circuitbreaker.dto.UserDTO;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class UserDataService {

    private static final Logger logger = LoggerFactory.getLogger(UserDataService.class);

    private final RestTemplate restTemplate;
    private final String externalBaseUrl;

    public UserDataService(RestTemplate restTemplate,
                           @Value("${app.external.base-url}") String externalBaseUrl) {
        this.restTemplate = restTemplate;
        this.externalBaseUrl = externalBaseUrl;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getFallbackUserData")
    public UserDTO fetchUser(String id) {
        String url = UriComponentsBuilder.fromHttpUrl(externalBaseUrl)
                .pathSegment("users", id)
                .toUriString();

        logger.info("Fetching user {} from external service at {}", id, url);
        return restTemplate.getForObject(url, UserDTO.class);
    }

    public UserDTO getFallbackUserData(String id, Throwable throwable) {
        logger.warn("Fallback triggered for user {} because: {}", id, throwable.getMessage());
        return new UserDTO("default-id", "Default User", "default@example.com");
    }
}
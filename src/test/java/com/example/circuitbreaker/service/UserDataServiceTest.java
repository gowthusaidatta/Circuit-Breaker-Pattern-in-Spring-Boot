package com.example.circuitbreaker.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import com.example.circuitbreaker.dto.UserDTO;

class UserDataServiceTest {

    @Test
    void fetchUserDelegatesToRestTemplate() {
        RestTemplate rest = new RestTemplate() {
            @Override
            public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables) {
                if ("http://example.com/users/1".equals(url) && responseType == UserDTO.class) {
                    return responseType.cast(new UserDTO("1", "Alice", "a@ex.com"));
                }
                return null;
            }
        };

        UserDataService svc = new UserDataService(rest, "http://example.com");
        UserDTO user = svc.fetchUser("1");

        assertNotNull(user);
        assertEquals("1", user.id());
    }

    @Test
    void getFallbackUserDataReturnsDefault() {
        RestTemplate rest = new RestTemplate();
        UserDataService svc = new UserDataService(rest, "http://example.com");

        UserDTO fallback = svc.getFallbackUserData("x", new RuntimeException("boom"));
        assertEquals("default-id", fallback.id());
        assertEquals("Default User", fallback.name());
    }
}

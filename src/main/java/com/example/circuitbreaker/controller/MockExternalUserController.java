package com.example.circuitbreaker.controller;

import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.circuitbreaker.dto.UserDTO;
import com.example.circuitbreaker.service.MockExternalBackendState;
import com.example.circuitbreaker.service.MockExternalBackendState.Mode;

@RestController
@RequestMapping("/mock/external")
public class MockExternalUserController {

    private final MockExternalBackendState backendState;

    public MockExternalUserController(MockExternalBackendState backendState) {
        this.backendState = backendState;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String id) {
        if (backendState.getMode() == Mode.FAILING) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Mock external service failure");
        }

        return ResponseEntity.ok(new UserDTO(id, "Mock User " + id, "user" + id + "@example.com"));
    }

    @PostMapping("/mode/{mode}")
    public ResponseEntity<Map<String, String>> setMode(@PathVariable String mode) {
        backendState.setMode(Mode.fromValue(mode));
        return ResponseEntity.ok(Map.of("mode", backendState.getMode().name().toLowerCase(Locale.ROOT)));
    }
}
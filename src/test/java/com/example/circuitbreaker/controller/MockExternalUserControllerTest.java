package com.example.circuitbreaker.controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import com.example.circuitbreaker.dto.UserDTO;
import com.example.circuitbreaker.service.MockExternalBackendState;

class MockExternalUserControllerTest {

    @Test
    void getUserReturnsUserWhenHealthy() {
        MockExternalBackendState backend = new MockExternalBackendState();
        MockExternalUserController controller = new MockExternalUserController(backend);

        ResponseEntity<UserDTO> resp = controller.getUser("42");
        assertEquals(200, resp.getStatusCodeValue());
        assertEquals("42", resp.getBody().id());
    }

    @Test
    void getUserThrowsWhenFailing() {
        MockExternalBackendState backend = new MockExternalBackendState();
        backend.setMode(MockExternalBackendState.Mode.FAILING);
        MockExternalUserController controller = new MockExternalUserController(backend);

        assertThrows(ResponseStatusException.class, () -> controller.getUser("1"));
    }

    @Test
    void setModeUpdatesBackendMode() {
        MockExternalBackendState backend = new MockExternalBackendState();
        MockExternalUserController controller = new MockExternalUserController(backend);

        ResponseEntity<java.util.Map<String, String>> resp = controller.setMode("failing");
        assertEquals("failing", resp.getBody().get("mode"));
        assertEquals(MockExternalBackendState.Mode.FAILING, backend.getMode());
    }
}

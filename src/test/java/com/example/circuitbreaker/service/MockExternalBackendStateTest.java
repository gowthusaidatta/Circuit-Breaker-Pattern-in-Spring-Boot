package com.example.circuitbreaker.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MockExternalBackendStateTest {

    @Test
    void modeFromValueAcceptsCommonValues() {
        assertEquals(MockExternalBackendState.Mode.HEALTHY, MockExternalBackendState.Mode.fromValue("healthy"));
        assertEquals(MockExternalBackendState.Mode.HEALTHY, MockExternalBackendState.Mode.fromValue("OK"));
        assertEquals(MockExternalBackendState.Mode.FAILING, MockExternalBackendState.Mode.fromValue("fail"));
    }

    @Test
    void modeFromValueThrowsOnUnsupported() {
        assertThrows(IllegalArgumentException.class, () -> MockExternalBackendState.Mode.fromValue("unknown-value"));
    }

    @Test
    void getAndSetModeWorks() {
        MockExternalBackendState state = new MockExternalBackendState();
        assertEquals(MockExternalBackendState.Mode.HEALTHY, state.getMode());
        state.setMode(MockExternalBackendState.Mode.FAILING);
        assertEquals(MockExternalBackendState.Mode.FAILING, state.getMode());
    }
}

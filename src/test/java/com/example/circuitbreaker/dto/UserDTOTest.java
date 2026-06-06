package com.example.circuitbreaker.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserDTOTest {

    @Test
    void recordStoresValuesAndEqualsWorks() {
        UserDTO a = new UserDTO("1", "Alice", "alice@example.com");
        UserDTO b = new UserDTO("1", "Alice", "alice@example.com");

        assertEquals("1", a.id());
        assertEquals("Alice", a.name());
        assertEquals("alice@example.com", a.email());
        assertEquals(a, b);
    }
}

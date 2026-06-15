package com.Ntm.TestUnitarios.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdminAccessResponseTest {
    @Test
    void shouldSetAndGetValues() {
        AdminAccessResponse response = new AdminAccessResponse();

        response.setRoomId("ROOM-123");
        response.setAdminName("Dyssio");

        assertEquals("ROOM-123", response.getRoomId());
        assertEquals("Dyssio", response.getAdminName());
    }

    @Test
    void shouldCreateUsingConstructor() {
        AdminAccessResponse response = new AdminAccessResponse("ROOM-123", "Dyssio");

        assertEquals("ROOM-123", response.getRoomId());
        assertEquals("Dyssio", response.getAdminName());
    }
}
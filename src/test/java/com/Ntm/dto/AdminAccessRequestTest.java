package com.Ntm.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdminAccessRequestTest {
    @Test
    void shouldSetAndGetFields() {
        AdminAccessRequest request = new AdminAccessRequest();

        request.setRoomId("ROOM-123");
        request.setMasterKey("KEY");
        request.setUserName("Dyssio");

        assertEquals("ROOM-123", request.getRoomId());
        assertEquals("KEY", request.getMasterKey());
        assertEquals("Dyssio", request.getUserName());
    }

    @Test
    void shouldCreateUsingConstructor() {
        AdminAccessRequest request =
                new AdminAccessRequest("ROOM-123", "KEY", "Dyssio");

        assertEquals("ROOM-123", request.getRoomId());
        assertEquals("KEY", request.getMasterKey());
        assertEquals("Dyssio", request.getUserName());
    }
}
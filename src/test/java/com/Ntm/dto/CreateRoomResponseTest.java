package com.Ntm.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CreateRoomResponseTest {
    @Test
    void shouldReturnConstructorValues() {
        CreateRoomResponse response = new CreateRoomResponse(
                        "ROOM-123", "MK-12345", "Sala Test");

        assertEquals("ROOM-123", response.getRoomId());
        assertEquals("MK-12345", response.getMasterKey());
        assertEquals("Sala Test", response.getRoomName());
    }
}
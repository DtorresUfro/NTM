package com.Ntm.TestUnitarios.dto;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class JoinRoomResponseTest {
    @Test
    void shouldReturnConstructorValues() {
        JoinRoomResponse response = new JoinRoomResponse(
                "ROOM-123", "Sala Test", List.of("Valen", "Dyssio"));

        assertEquals("ROOM-123", response.getRoomId());
        assertEquals("Sala Test", response.getRoomName());
        assertEquals(2, response.getParticipants().size());
    }
}
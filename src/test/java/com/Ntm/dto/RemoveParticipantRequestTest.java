package com.Ntm.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RemoveParticipantRequestTest {
    @Test
    void shouldSetAndGetAllFields() {
        RemoveParticipantRequest request = new RemoveParticipantRequest();

        request.setRoomId("ROOM-123");
        request.setAdminName("Valen");
        request.setUsernameToRemove("Dyssio");

        assertEquals("ROOM-123", request.getRoomId());
        assertEquals("Valen", request.getAdminName());
        assertEquals("Dyssio", request.getUsernameToRemove());
    }
}
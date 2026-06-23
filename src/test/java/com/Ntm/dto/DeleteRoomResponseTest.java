package com.Ntm.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DeleteRoomResponseTest {
    @Test
    void shouldReturnMessage() {
        DeleteRoomResponse response = new DeleteRoomResponse("Sala eliminada");

        assertEquals("Sala eliminada", response.getMessage());
    }
}
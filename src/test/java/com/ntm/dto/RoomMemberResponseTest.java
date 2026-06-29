package com.ntm.dto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoomMemberResponseTest {

    @Test
    void shouldCreateRoomMemberUsingConstructor() {
        RoomMemberResponse response =
                new RoomMemberResponse("Ivan", true, true);
        assertEquals("Ivan", response.getUsername());
        assertTrue(response.isAdmin());
        assertTrue(response.isConnected());
    }

    @Test
    void shouldUseAllSettersAndGetters() {
        RoomMemberResponse response =
                new RoomMemberResponse();
        response.setUsername("Ivan");
        response.setAdmin(false);
        response.setConnected(true);

        assertEquals("Ivan", response.getUsername());
        assertFalse(response.isAdmin());
        assertTrue(response.isConnected());
    }
}
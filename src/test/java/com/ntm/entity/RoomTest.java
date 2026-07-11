package com.ntm.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    @Test
    void shouldUseSettersAndGetters() {
        Room room = new Room();

        room.setName("Sala");
        room.setAdminName("Ivan");
        room.setMasterKey("123");
        room.setCalendar(new Calendar());

        assertEquals("Sala", room.getName());
        assertEquals("Ivan", room.getAdminName());
        assertEquals("123", room.getMasterKey());
        assertNotNull(room.getCalendar());
    }

    @Test
    void shouldSetParticipantsList() {

        Room room = new Room();

        room.setParticipants(
                new ArrayList<>(List.of("Ivan", "Lucas")));

        assertEquals(2,
                room.getParticipants().size());
    }

    @Test
    void shouldInitializeCreationAndActivityDates() {
        Room room = new Room("Sala", "Ivan");

        assertNotNull(room.getCreatedAt());
        assertNotNull(room.getLastActivity());
        assertEquals(room.getCreatedAt(), room.getLastActivity());
    }

    @Test
    void shouldUpdateLastActivityWhenRoomHasActivity() {
        Room room = new Room("Sala", "Ivan");
        LocalDateTime previousActivity = LocalDateTime.of(2024, 1, 1, 0, 0);
        room.setLastActivity(previousActivity);

        room.markActivity();

        assertTrue(room.getLastActivity().isAfter(previousActivity));
        assertEquals("Sala", room.getName());
    }
}

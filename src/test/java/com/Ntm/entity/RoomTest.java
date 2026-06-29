package com.Ntm.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RoomTest {

    @Test
    void shouldUseSettersAndGetters() {
        Room room = new Room();

        room.setName("Sala");
        room.setAdminName("Ivan");
        room.setMasterKey("123");
        room.setCalendar(new Calendar());

        assertEquals("Sala",room.getName());
        assertEquals("Ivan",room.getAdminName());
        assertEquals("123",room.getMasterKey());
        assertNotNull(room.getCalendar());
    }

    @Test
    void shouldSetParticipantsList() {

        Room room = new Room();

        room.setParticipants(
                new ArrayList<>(List.of("Ivan","Lucas")));

        assertEquals(2,
                room.getParticipants().size());
    }
}

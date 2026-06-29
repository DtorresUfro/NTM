package com.ntm.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NoteRequestTest {
    @Test
    void shouldSetAndGetAllFields() {
        NoteRequest request = new NoteRequest();

        request.setRoomId("ROOM-123");
        request.setNoteTitle("Mi Nota");
        request.setUsername("Dyssio");
        request.setContent("Test¿");

        assertEquals("ROOM-123", request.getRoomId());
        assertEquals("Mi Nota", request.getNoteTitle());
        assertEquals("Dyssio", request.getUsername());
        assertEquals("Test¿", request.getContent());
    }
}
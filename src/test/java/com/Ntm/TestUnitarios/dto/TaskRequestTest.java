package com.Ntm.TestUnitarios.dto;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class TaskRequestTest {
    @Test
    void shouldSetAndGetAllFields() {
        Date dueDate = new Date();

        TaskRequest request = new TaskRequest();

        request.setRoomId("ROOM-123");
        request.setUsername("Dyssio");
        request.setTaskTitle("Implementar pruebas");
        request.setDescription("Tengo sueño T_T");
        request.setDueDate(dueDate);

        assertEquals("ROOM-123", request.getRoomId());
        assertEquals("Dyssio", request.getUsername());
        assertEquals("Implementar pruebas", request.getTaskTitle());
        assertEquals("Tengo sueño T_T", request.getDescription());
        assertEquals(dueDate, request.getDueDate());
    }
}
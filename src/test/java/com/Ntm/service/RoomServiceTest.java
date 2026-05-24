package com.Ntm.service;

import com.Ntm.dto.JoinRoomRequest;
import com.Ntm.dto.JoinRoomResponse;
import com.Ntm.entity.Room;
import com.Ntm.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomServiceTest {

    private RoomRepository roomRepository;
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        roomRepository = mock(RoomRepository.class);
        roomService = new RoomService(roomRepository);
    }

    @Test
    void shouldJoinRoomSuccessfully() {
        // 1. Preparamos el pedido (Request)
        JoinRoomRequest request = new JoinRoomRequest();
        setField(request, "roomId", "ABCD1234");
        setField(request, "username", "Lucas");

        // 2. Simulamos la sala existente en la base de datos
        Room mockRoom = new Room("Sala de Estudio", "Valen");
        setField(mockRoom, "id", "ABCD1234");

        // Configuramos el clon del repositorio para que devuelva la sala simulada
        when(roomRepository.findById("ABCD1234")).thenReturn(java.util.Optional.of(mockRoom));

        // 3. Actuamos
        JoinRoomResponse response = roomService.joinRoom(request);

        // 4. Verificamos
        assertNotNull(response);
        assertEquals("ABCD1234", response.getRoomId());
        assertEquals("Sala de Estudio", response.getRoomName());
        assertTrue(response.getParticipants().contains("Lucas"));
    }

    // Método de reflection para poder setear campos privados sin setters
    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
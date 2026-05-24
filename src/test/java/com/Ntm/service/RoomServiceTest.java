package com.Ntm.service;

import com.Ntm.dto.DeleteRoomRequest;
import com.Ntm.dto.DeleteRoomResponse;
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
    void shouldDeleteRoomSuccessfully() {
        // 1. Preparamos el pedido (Request) del administrador
        DeleteRoomRequest request = new DeleteRoomRequest();
        setField(request, "adminName", "Valen");

        // 2. Simulamos la sala que se va a borrar
        Room mockRoom = new Room("Sala Test", "Valen");
        setField(mockRoom, "id", "ABCD1234");

        // Configuramos el clon del repositorio para que encuentre la sala
        when(roomRepository.findById("ABCD1234")).thenReturn(java.util.Optional.of(mockRoom));

        // 3. Actuamos (Pasamos el ID y el request)
        DeleteRoomResponse response = roomService.deleteRoom("ABCD1234", request);

        // 4. Verificamos
        assertNotNull(response);
        assertNotNull(response.getMessage());

        // Verificamos que se llamó explícitamente a la orden de borrar en la BD
        verify(roomRepository).delete(mockRoom);
    }

    // Método Reflection para poder setear campos privados sin setters
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
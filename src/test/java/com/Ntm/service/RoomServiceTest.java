package com.Ntm.service;

import com.Ntm.dto.CreateRoomRequest;
import com.Ntm.dto.CreateRoomResponse;
import com.Ntm.entity.Room;
import com.Ntm.exception.InvalidRoomException;
import com.Ntm.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

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
    void shouldCreateRoomSuccessfully() {
        CreateRoomRequest request = new CreateRoomRequest();

// Simular datos usando reflection
        setField(request, "roomName", "Sala Test");
        setField(request, "adminName", "Valen");

        CreateRoomResponse response = roomService.createRoom(request);

        assertNotNull(response);
        assertEquals("Sala Test", response.getRoomName());
        assertNotNull(response.getRoomId());
        assertNotNull(response.getMasterKey());

        ArgumentCaptor<Room> captor = ArgumentCaptor.forClass(Room.class);
        verify(roomRepository).save(captor.capture());

        Room savedRoom = captor.getValue();

        assertEquals("Sala Test", savedRoom.getName());
        assertEquals("Valen", savedRoom.getAdminName());
        assertTrue(savedRoom.getParticipants().contains("Valen"));
    }

    @Test
    void shouldThrowExceptionWhenRoomNameIsEmpty() {
        CreateRoomRequest request = new CreateRoomRequest();

        setField(request, "roomName", "");
        setField(request, "adminName", "Valen");

        InvalidRoomException exception = assertThrows(
                InvalidRoomException.class,
                () -> roomService.createRoom(request)
        );

        assertEquals("El nombre de la sala es obligatorio", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAdminNameIsEmpty() {
        CreateRoomRequest request = new CreateRoomRequest();

        setField(request, "roomName", "Sala Test");
        setField(request, "adminName", "");

        InvalidRoomException exception = assertThrows(
                InvalidRoomException.class,
                () -> roomService.createRoom(request)
        );

        assertEquals("El nombre del administrador es obligatorio", exception.getMessage());
    }

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
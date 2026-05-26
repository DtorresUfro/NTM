package com.Ntm.service;

import com.Ntm.dto.*;
import com.Ntm.entity.Room;
import com.Ntm.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

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

    /**
     * CASO DE USO 1: Crear Sala
     */
    @Test
    void shouldCreateRoomSuccessfully() {
        CreateRoomRequest request = new CreateRoomRequest();
        setField(request, "roomName", "Sala de Valen");
        setField(request, "adminName", "Valen");

        // Simulamos que al guardar en la BD, la entidad recibe un ID
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> {
            Room room = invocation.getArgument(0);
            setField(room, "id", "NEW-1234");
            return room;
        });

        CreateRoomResponse response = roomService.createRoom(request);

        assertNotNull(response);
        assertEquals("Sala de Valen", response.getRoomName());
        assertNotNull(response.getRoomId());
        assertNotNull(response.getMasterKey());
        verify(roomRepository).save(any(Room.class));
    }

    /**
     * CASO DE USO 2: Unirse a Sala
     */
    @Test
    void shouldJoinRoomSuccessfully() {
        JoinRoomRequest request = new JoinRoomRequest();
        setField(request, "roomId", "ABCD1234");
        setField(request, "username", "Lucas");

        Room mockRoom = new Room("Sala de Estudio", "Valen");
        setField(mockRoom, "id", "ABCD1234");

        when(roomRepository.findById("ABCD1234")).thenReturn(Optional.of(mockRoom));

        JoinRoomResponse response = roomService.joinRoom(request);

        assertNotNull(response);
        assertEquals("ABCD1234", response.getRoomId());
        assertEquals("Sala de Estudio", response.getRoomName());
        assertTrue(response.getParticipants().contains("Lucas"));
    }

    /**
     * CASO DE USO 3: Eliminar Sala
     */
    @Test
    void shouldDeleteRoomSuccessfully() {
        DeleteRoomRequest request = new DeleteRoomRequest();
        setField(request, "adminName", "Valen");

        Room mockRoom = new Room("Sala Test", "Valen");
        setField(mockRoom, "id", "ABCD1234");

        when(roomRepository.findById("ABCD1234")).thenReturn(Optional.of(mockRoom));

        DeleteRoomResponse response = roomService.deleteRoom("ABCD1234", request);

        assertNotNull(response);
        assertNotNull(response.getMessage());
        verify(roomRepository).delete(mockRoom);
    }

    // Método de Reflection para poder setear campos privados sin setters
    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * CASO DE USO 4: Acceso Administrativo (Validar Master Key)
     */
    @Test
    void shouldGrantAdminAccessSuccessfully() {
        AdminAccessRequest request = new AdminAccessRequest();
        setField(request, "roomId", "SALA-123");
        setField(request, "masterKey", "secret123");
        setField(request, "userName", "Valen");

        Room mockRoom = new Room("Sala Secreta", "Admin");
        setField(mockRoom, "id", "SALA-123");
        setField(mockRoom, "masterKey", "secret123");

        when(roomRepository.findById("SALA-123")).thenReturn(Optional.of(mockRoom));

        AdminAccessResponse response = roomService.grantAdminAccess(request);

        assertNotNull(response);
        assertEquals("SALA-123", response.getRoomId());
        assertEquals("ADMIN", response.getRole());
        assertTrue(mockRoom.getParticipants().contains("Valen"));
        // Verifica que se añadió al participante
        verify(roomRepository).save(mockRoom);
        // Verifica que se guardaron los cambios
    }

    @Test
    void shouldRemoveParticipantSuccessfully() {
        // 1. Preparar datos
        String roomId = "ROOM-123";
        String adminName = "Valen";
        String userToRemove = "Lucas";

        RemoveParticipantRequest request = new RemoveParticipantRequest();
        setField(request, "roomId", roomId);
        setField(request, "adminName", adminName);
        setField(request, "usernameToRemove", userToRemove);

        // 2. Crear sala con el admin y el usuario a eliminar
        Room mockRoom = new Room("Sala de Estudio", adminName);
        setField(mockRoom, "id", roomId);
        mockRoom.getParticipants().add("Lucas"); // Aseguramos que Lucas está dentro

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(mockRoom));

        // 3. Ejecutar
        roomService.removeParticipant(request);

        // 4. Verificar
        assertFalse(mockRoom.getParticipants().contains(userToRemove), "El usuario debería haber sido eliminado");
        verify(roomRepository).save(mockRoom);
    }

    /**
     * CASO DE USO 7: Eliminar usuario de la sala
     */

    @Test
    void shouldThrowExceptionWhenNotAdminTriesToRemove() {
        String roomId = "ROOM-123";
        String intruder = "Lucas"; // Lucas no es el admin

        RemoveParticipantRequest request = new RemoveParticipantRequest();
        setField(request, "roomId", roomId);
        setField(request, "adminName", intruder); // Él se intenta hacer pasar por admin
        setField(request, "usernameToRemove", "OtroUsuario");

        Room mockRoom = new Room("Sala de Estudio", "Valen"); // El admin es Valen
        setField(mockRoom, "id", roomId);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(mockRoom));

        // 5. Verificar que lance la excepción de seguridad
        assertThrows(RuntimeException.class, () -> roomService.removeParticipant(request));
    }
}
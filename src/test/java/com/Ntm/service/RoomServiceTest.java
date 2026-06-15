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

    // Excepción por nombre de sala vacío
    @Test
    void shouldThrowExceptionWhenRoomNameIsEmpty() {
        CreateRoomRequest request = new CreateRoomRequest();
        setField(request, "roomName", "");
        setField(request, "adminName", "Valen");

        assertThrows(RuntimeException.class,
                () -> roomService.createRoom(request));

        verify(roomRepository, never()).save(any());
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

        when(roomRepository.findByRoomId("ABCD1234")).thenReturn(Optional.of(mockRoom));

        JoinRoomResponse response = roomService.joinRoom(request);

        assertNotNull(response);
        assertEquals("ABCD1234", response.getRoomId());
        assertEquals("Sala de Estudio", response.getRoomName());
        assertTrue(response.getParticipants().contains("Lucas"));
    }


     // Usuario duplicado en la sala
    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExistsInRoom() {

        JoinRoomRequest request = new JoinRoomRequest();
        setField(request, "roomId", "ROOM-123");
        setField(request, "username", "Lucas");

        Room room = new Room("Sala Test", "Valen");
        setField(room, "id", "ROOM-123");

        room.getParticipants().add("Lucas");

        when(roomRepository.findById("ROOM-123"))
                .thenReturn(Optional.of(room));

        assertThrows(RuntimeException.class,
                () -> roomService.joinRoom(request));

        verify(roomRepository, never()).save(any());
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

        when(roomRepository.findByRoomId("ABCD1234")).thenReturn(Optional.of(mockRoom));

        DeleteRoomResponse response = roomService.deleteRoom("ABCD1234", request);

        assertNotNull(response);
        assertNotNull(response.getMessage());
        verify(roomRepository).delete(mockRoom);
    }

     //Usuario sin permisos intenta eliminar sala
    @Test
    void shouldThrowExceptionWhenNotAdminTriesToDeleteRoom() {

        DeleteRoomRequest request = new DeleteRoomRequest();
        setField(request, "adminName", "Lucas");

        Room room = new Room("Sala Test", "Valen");
        setField(room, "id", "ROOM-123");

        when(roomRepository.findById("ROOM-123"))
                .thenReturn(Optional.of(room));

        assertThrows(RuntimeException.class,
                () -> roomService.deleteRoom("ROOM-123", request));

        verify(roomRepository, never()).delete(any());
    }

    // Metodo de Reflection para poder setear campos privados sin setters
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
        setField(request, "masterKey", "secret123");

        Room mockRoom = new Room("Sala Secreta", "Valen");
        setField(mockRoom, "id", "SALA-123");
        setField(mockRoom, "masterKey", "secret123");

        when(roomRepository.findByMasterKey("secret123")).thenReturn(Optional.of(mockRoom));

        AdminAccessResponse response = roomService.grantAdminAccess(request);

        assertNotNull(response);
        assertEquals("SALA-123", response.getRoomId());
        assertEquals("Valen", response.getAdminName());

        verify(roomRepository).findByMasterKey("secret123");
    }


    //Master Key incorrecta
    @Test
    void shouldThrowExceptionWhenMasterKeyIsInvalid() {

        AdminAccessRequest request = new AdminAccessRequest();

        setField(request, "roomId", "ROOM-123");
        setField(request, "masterKey", "claveIncorrecta");
        setField(request, "userName", "Lucas");

        Room room = new Room("Sala Test", "Valen");

        setField(room, "id", "ROOM-123");
        setField(room, "masterKey", "claveCorrecta");

        when(roomRepository.findById("ROOM-123"))
                .thenReturn(Optional.of(room));

        assertThrows(RuntimeException.class,
                () -> roomService.grantAdminAccess(request));

        verify(roomRepository, never()).save(any());
    }
    /**
     * CASO DE USO 5: Gestión de notas
     */

    //Crear la nota correctamente

    void shouldCreateNoteOrTaskSuccessfully() {

        // 1. Preparar solicitud
        NoteRequest request = new NoteRequest();

        setField(request, "roomId", "ROOM-123");
        setField(request, "username", "Lucas");
        setField(request, "noteTitle", "Entrega Proyecto");
        setField(request, "content", "Terminar pruebas unitarias");

        // 2. Crear sala simulada
        Room room = new Room("Sala Test", "Valen");
        setField(room, "id", "ROOM-123");

        room.getParticipants().add("Lucas");

        when(roomRepository.findById("ROOM-123"))
                .thenReturn(Optional.of(room));

        // 3. Ejecutar
        roomService.createNote(request);

        // 4. Verificar
        assertNotNull(room.getCalendar());
        assertEquals(1, room.getCalendar().getNotes().size());

        var note = room.getCalendar().getNotes().get(0);

        assertEquals("Entrega Proyecto", note.getTitle());
        assertEquals("Terminar pruebas unitarias", note.getContent());
        assertEquals("Lucas", note.getCreatedBy());

        verify(roomRepository).save(room);
    }

    // Excepcion si la sala no existe
    @Test
    void shouldThrowExceptionWhenRoomDoesNotExist() {

        NoteRequest request = new NoteRequest();

        setField(request, "roomId", "ROOM-INEXISTENTE");

        when(roomRepository.findById("ROOM-INEXISTENTE"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> roomService.createNote(request));
    }

    //Eliminar la nota correctamente
    @Test
    void shouldDeleteNoteSuccessfully() {

        NoteRequest request = new NoteRequest();

        setField(request, "roomId", "ROOM-123");
        setField(request, "username", "Lucas");
        setField(request, "noteTitle", "Nota 1");

        Room room = new Room("Sala Test", "Valen");
        setField(room, "id", "ROOM-123");

        com.Ntm.entity.Calendar calendar = new com.Ntm.entity.Calendar();

        calendar.addNote(
                new com.Ntm.entity.Note(
                        "Nota 1",
                        "Contenido",
                        "Lucas"));

        room.setCalendar(calendar);

        when(roomRepository.findByRoomId("ROOM-123"))
                .thenReturn(Optional.of(room));

        roomService.deleteNote(request);

        verify(roomRepository).save(room);
    }

    // Lanzar una excepcion cuando el usuario edita la nota de otro usuario

    @Test
    void shouldThrowExceptionWhenUserEditsAnotherUsersNote() {

        NoteRequest request = new NoteRequest();

        setField(request, "roomId", "ROOM-123");
        setField(request, "username", "Lucas");
        setField(request, "noteTitle", "Nota Barb");
        setField(request, "content", "Nuevo contenido");

        Room room = new Room("Sala Test", "Valen");
        setField(room, "id", "ROOM-123");

        com.Ntm.entity.Calendar calendar = new com.Ntm.entity.Calendar();

        calendar.addNote(
                new com.Ntm.entity.Note(
                        "Nota Barb",
                        "Contenido original",
                        "Barb"));

        room.setCalendar(calendar);

        when(roomRepository.findById("ROOM-123"))
                .thenReturn(Optional.of(room));

        assertThrows(RuntimeException.class,
                () -> roomService.updateNote(request));

        verify(roomRepository, never()).save(any());
    }

    /**
     * CASO DE USO 7: Eliminar usuario de la sala
     */

    //Eliminar usuario de la sala correctamente
    @Test
    void shouldRemoveParticipantSuccessfully() {

        String roomId = "ROOM-123";
        String adminName = "Valen";
        String userToRemove = "Lucas";

        RemoveParticipantRequest request = new RemoveParticipantRequest();

        setField(request, "roomId", roomId);
        setField(request, "adminName", adminName);
        setField(request, "usernameToRemove", userToRemove);

        Room room = new Room("Sala de Estudio", adminName);
        setField(room, "id", roomId);

        room.getParticipants().add("Lucas");

        when(roomRepository.findByRoomId(roomId))
                .thenReturn(Optional.of(room));

        // Ejecutar acción
        roomService.removeParticipant(request);

        // Verificar eliminación
        assertFalse(room.getParticipants().contains(userToRemove));

        verify(roomRepository).save(room);
    }

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
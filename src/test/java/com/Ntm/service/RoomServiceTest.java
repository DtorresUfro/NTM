package com.Ntm.service;

import com.Ntm.dto.*;
import com.Ntm.entity.Calendar;
import com.Ntm.entity.Note;
import com.Ntm.entity.Room;
import com.Ntm.entity.Task;
import com.Ntm.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomServiceTest {

    private RoomRepository roomRepository;
    private RoomService roomService;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        roomRepository = mock(RoomRepository.class);
        notificationService = mock(NotificationService.class);
        roomService = new RoomService(roomRepository, null, notificationService);
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

        when(roomRepository.findByRoomId("ROOM-123"))
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

        when(roomRepository.findByRoomId("ROOM-123"))
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

        AdminAccessResponse response = roomService.validateMasterKey(request);
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

        when(roomRepository.findByRoomId("ROOM-123"))
                .thenReturn(Optional.of(room));

        assertThrows(RuntimeException.class,
                () -> roomService.validateMasterKey(request));

        verify(roomRepository, never()).save(any());
    }

    //Master Key innexistente
    @Test
    void shouldThrowExceptionWhenMasterKeyDoesNotExist() {
        AdminAccessRequest request = new AdminAccessRequest();
        request.setMasterKey("incorrecta");

        when(roomRepository.findByMasterKey("incorrecta")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> roomService.validateMasterKey(request));    }

    /**
     * CASO DE USO 5: Gestión de notas
     */

    //Crear la nota correctamente
    @Test
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

        when(roomRepository.findByRoomId("ROOM-123"))
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

        when(roomRepository.findByRoomId("ROOM-INEXISTENTE"))
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

        Calendar calendar = new Calendar();

        calendar.addNote(
                new Note(
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

        Calendar calendar = new Calendar();

        calendar.addNote(
                new Note(
                        "Nota Barb",
                        "Contenido original",
                        "Barb"));

        room.setCalendar(calendar);

        when(roomRepository.findByRoomId("ROOM-123"))
                .thenReturn(Optional.of(room));

        assertThrows(RuntimeException.class,
                () -> roomService.updateNote(request));

        verify(roomRepository, never()).save(any());
    }

    // Crear calendario si la Sala no tiene uno
    @Test
    void shouldCreateCalendarWhenRoomHasNoCalendar() {

        NoteRequest request = new NoteRequest();

        setField(request, "roomId", "ROOM-123");
        setField(request, "username", "Lucas");
        setField(request, "noteTitle", "Nota");
        setField(request, "content", "Contenido");

        Room room = new Room("Sala Test", "Ivan");
        setField(room, "id", "ROOM-123");

        room.getParticipants().add("Lucas");

        room.setCalendar(null);

        when(roomRepository.findByRoomId("ROOM-123"))
                .thenReturn(Optional.of(room));

        roomService.createNote(request);

        assertNotNull(room.getCalendar());
        assertEquals(1, room.getCalendar().getNotes().size());

        verify(roomRepository).save(room);
    }

    //Una nota está vinculada al calendario
    @Test
    void shouldAssociateCreatedNoteWithCalendar() {

        NoteRequest request = new NoteRequest();

        setField(request, "roomId", "ROOM-123");
        setField(request, "username", "Lucas");
        setField(request, "noteTitle", "Reunión");
        setField(request, "content", "Viernes");

        Room room = new Room("Sala Test", "Ivan");
        setField(room, "id", "ROOM-123");

        room.getParticipants().add("Lucas");

        Calendar calendar = new Calendar();
        room.setCalendar(calendar);

        when(roomRepository.findByRoomId("ROOM-123"))
                .thenReturn(Optional.of(room));

        roomService.createNote(request);

        Note note = calendar.getNotes().get(0);

        assertSame(calendar, note.getCalendar());
    }

    //Una tarea está vinculada al calendario
    @Test
    void shouldAssociateTaskWithCalendar() {

        Calendar calendar = new Calendar();

        Room room = new Room("Sala Test","Ivan");

        setField(room,"id","ROOM-123");

        room.getParticipants().add("Lucas");

        room.setCalendar(calendar);

        TaskRequest request = new TaskRequest();

        setField(request,"roomId","ROOM-123");
        setField(request,"username","Lucas");
        setField(request,"taskTitle","API");
        setField(request,"description","REST");
        setField(request,"startDate",new Date());
        setField(request,"dueDate",new Date());

        when(roomRepository.findByRoomId("ROOM-123"))
                .thenReturn(Optional.of(room));
        roomService.createTask(request);

        Task task = calendar.getTasks().get(0);

        assertSame(calendar, task.getCalendar());

    }

    //Agregar varias notas a un solo calendario
    @Test
    void shouldStoreMultipleNotesInSameCalendar() {

        Room room = new Room("Sala Test", "Valen");
        setField(room, "id", "ROOM-123");

        room.getParticipants().add("Lucas");

        Calendar calendar = new Calendar();
        room.setCalendar(calendar);

        when(roomRepository.findByRoomId("ROOM-123"))
                .thenReturn(Optional.of(room));

        NoteRequest request1 = new NoteRequest();
        setField(request1,"roomId","ROOM-123");
        setField(request1,"username","Lucas");
        setField(request1,"noteTitle","Nota 1");
        setField(request1,"content","Contenido 1");

        NoteRequest request2 = new NoteRequest();
        setField(request2,"roomId","ROOM-123");
        setField(request2,"username","Lucas");
        setField(request2,"noteTitle","Nota 2");
        setField(request2,"content","Contenido 2");

        roomService.createNote(request1);
        roomService.createNote(request2);

        assertEquals(2, calendar.getNotes().size());
    }

    //Si se elimina una nota, se elimina del calendario
    @Test
    void shouldRemoveNoteFromCalendarWhenDeletingNote() {

        Calendar calendar = new Calendar();

        Note note = new Note(
                "Nota importante",
                "Contenido",
                "Lucas");

        calendar.addNote(note);

        Room room = new Room("Sala Test", "Valen");
        setField(room, "id", "ROOM-123");

        room.getParticipants().add("Lucas");
        room.setCalendar(calendar);

        when(roomRepository.findByRoomId("ROOM-123"))
                .thenReturn(Optional.of(room));

        NoteRequest request = new NoteRequest();

        setField(request, "roomId", "ROOM-123");
        setField(request, "username", "Lucas");
        setField(request, "noteTitle", "Nota importante");

        roomService.deleteNote(request);

        assertEquals(0, calendar.getNotes().size());

        verify(roomRepository).save(room);
    }

    /**
     * CASO DE USO 6: Recibir notificaciones
     */

    //Se crea notificacion si la tarea está atrasada
    @Test
    void shouldCreateNotificationWhenTaskIsOverdue() {
        Room room = new Room("Sala Test", "Valen");
        room.setId("ROOM-123");
        room.getParticipants().add("Ivan");
        room.getParticipants().add("Dyssio");

        when(roomRepository.findByRoomId("ROOM-123")).thenReturn(Optional.of(room));

        TaskRequest request = new TaskRequest();
        request.setRoomId("ROOM-123");
        request.setUsername("Ivan");
        request.setTaskTitle("Tarea atrasada");
        request.setDescription("Esta tarea esta atrasada");
        request.setDueDate(new Date(System.currentTimeMillis() - 86400000));

        roomService.createTask(request);

        verify(notificationService).createTaskNotification(any(Task.class), eq("ROOM-123"), eq("Ivan"));
    }

    //Si una tarea no está atrasada, no se crea notificacion
    @Test
    void shouldNotCreateNotificationWhenTaskIsNotOverdue() {
        Room room = new Room("Sala Test", "Valen");
        room.setId("ROOM-123");
        room.getParticipants().add("Lucas");

        when(roomRepository.findByRoomId("ROOM-123")).thenReturn(Optional.of(room));

        TaskRequest request = new TaskRequest();
        request.setRoomId("ROOM-123");
        request.setUsername("Lucas");
        request.setTaskTitle("Tarea futura");
        request.setDescription("Esta tarea no esta atrasada");
        request.setDueDate(new Date(System.currentTimeMillis() + 86400000));

        roomService.createTask(request);

        verify(notificationService, never()).createTaskNotification(any(Task.class), anyString(), anyString());
    }

    @Test
    void shouldNotifyAllParticipantsWhenTaskIsOverdue() {
        Room room = new Room("Sala Test", "Valen");
        room.setId("ROOM-123");
        room.getParticipants().add("Ivan");
        room.getParticipants().add("Dyssio");
        room.getParticipants().add("Valen");

        when(roomRepository.findByRoomId("ROOM-123")).thenReturn(Optional.of(room));

        TaskRequest request = new TaskRequest();
        request.setRoomId("ROOM-123");
        request.setUsername("Dyssio");
        request.setTaskTitle("Tarea atrasada");
        request.setDueDate(new Date(System.currentTimeMillis() - 86400000));

        roomService.createTask(request);

        verify(notificationService, times(3)).createTaskNotification(
                any(Task.class),
                eq("ROOM-123"),
                anyString()
        );
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

        when(roomRepository.findByRoomId(roomId)).thenReturn(Optional.of(mockRoom));

        // 5. Verificar que lance la excepción de seguridad
        assertThrows(RuntimeException.class, () -> roomService.removeParticipant(request));
    }

    //Actualizar nota creada por el mismo usuario
    @Test
    void shouldUpdateNoteSuccessfully() {
        NoteRequest request = new NoteRequest();

        request.setRoomId("ROOM-123");
        request.setUsername("Dyssio");
        request.setNoteTitle("Nota 1");
        request.setContent("Contenido actualizado");

        Room room = new Room("Sala", "Valen");
        setField(room, "id", "ROOM-123");

        Calendar calendar = new Calendar();

        Note note = new Note("Nota 1", "Contenido antiguo", "Dyssio");

        calendar.addNote(note);
        room.setCalendar(calendar);

        when(roomRepository.findByRoomId("ROOM-123")).thenReturn(Optional.of(room));

        roomService.updateNote(request);

        assertEquals("Contenido actualizado", note.getContent());

        verify(roomRepository).save(room);
    }

    //Verifica que se impida eliminar una nota cuando el usuario no es su creador.
    @Test
    void shouldThrowExceptionWhenDeletingOtherUsersNote() {
        NoteRequest request = new NoteRequest();

        request.setRoomId("ROOM-123");
        request.setUsername("Dyssio");
        request.setNoteTitle("Nota 1");

        Room room = new Room("Sala", "Valen");
        setField(room, "id", "ROOM-123");

        Calendar calendar = new Calendar();

        calendar.addNote(new Note("Nota 1", "Contenido", "Ivan"));

        room.setCalendar(calendar);

        when(roomRepository.findByRoomId("ROOM-123")).thenReturn(Optional.of(room));

        assertThrows(RuntimeException.class, () -> roomService.deleteNote(request));
    }

    //Crear tarea
    @Test
    void shouldCreateTaskSuccessfully() {
        TaskRequest request = new TaskRequest();

        request.setRoomId("ROOM-123");
        request.setUsername("Dyssio");
        request.setTaskTitle("Tarea 1");
        request.setDescription("Descripción");
        request.setDueDate(new Date());

        Room room = new Room("Sala", "Valen");
        setField(room, "id", "ROOM-123");

        room.getParticipants().add("Dyssio");

        when(roomRepository.findByRoomId("ROOM-123")).thenReturn(Optional.of(room));

        roomService.createTask(request);

        assertNotNull(room.getCalendar());
        assertEquals(1, room.getCalendar().getTasks().size());

        verify(roomRepository).save(room);
    }

    //Actualizar tarea
    @Test
    void shouldUpdateTaskSuccessfully() {
        TaskRequest request = new TaskRequest();

        request.setRoomId("ROOM-123");
        request.setTaskTitle("Tarea 1");
        request.setDescription("Nueva");
        request.setDueDate(new Date());

        Room room = new Room("Sala", "Valen");
        setField(room, "id", "ROOM-123");

        Calendar calendar = new Calendar();

        calendar.addTask(new Task("Tarea 1", "Vieja",
                new Date(), "Dyssio", new Date(), false));

        room.setCalendar(calendar);

        when(roomRepository.findByRoomId("ROOM-123")).thenReturn(Optional.of(room));

        roomService.updateTask(request);

        verify(roomRepository).save(room);
    }

    //Marcar tarea como completada
    @Test
    void shouldCompleteTaskSuccessfully() {
        Task task = new Task("Tarea 1", "Desc",
                new Date(), "Lucas", new Date(), false);

        Calendar calendar = new Calendar();
        calendar.addTask(task);

        Room room = new Room("Sala", "Valen");
        setField(room, "id", "ROOM-123");
        room.setCalendar(calendar);

        TaskRequest request = new TaskRequest();
        request.setRoomId("ROOM-123");
        request.setTaskTitle("Tarea 1");

        when(roomRepository.findByRoomId("ROOM-123")).thenReturn(Optional.of(room));

        roomService.completeTask(request);

        assertTrue(task.isCompleted());

        verify(roomRepository).save(room);
    }

    //Eliminar tarea
    @Test
    void shouldDeleteTaskSuccessfully() {
        Task task = new Task("Tarea 1", "Desc",
                new Date(), "Lucas", new Date(), false);

        Calendar calendar = new Calendar();
        calendar.addTask(task);

        Room room = new Room("Sala", "Valen");
        setField(room, "id", "ROOM-123");
        room.setCalendar(calendar);

        TaskRequest request = new TaskRequest();
        request.setRoomId("ROOM-123");
        request.setUsername("Lucas");
        request.setTaskTitle("Tarea 1");

        when(roomRepository.findByRoomId("ROOM-123")).thenReturn(Optional.of(room));

        roomService.deleteTask(request);

        assertFalse(task.isActive());
        verify(roomRepository).save(room);
    }

    //Obtener participantes de la sala
    @Test
    void shouldReturnParticipants() {
        Room room = new Room("Sala", "Valen");
        setField(room, "id", "ROOM-123");

        room.addParticipant("Lucas");

        when(roomRepository.findByRoomId("ROOM-123")).thenReturn(Optional.of(room));

        var result = roomService.getRoomParticipants("ROOM-123");

        assertEquals("Valen (Admin)", result.get(0));
        assertTrue(result.contains("Lucas"));
    }

    //Obtener tareas
    @Test
    void shouldReturnTasksOfRoom() {

        Calendar calendar = new Calendar();

        calendar.addTask(new Task(
                "API",
                "REST",
                new Date(),
                "Lucas",
                new Date(),
                false));
        Room room = new Room("Sala", "Valen");
        setField(room, "id", "ROOM-123");
        room.setCalendar(calendar);

        when(roomRepository.findByRoomId("ROOM-123"))
                .thenReturn(Optional.of(room));
        assertEquals(1,
                roomService.getTasksByRoom("ROOM-123").size());
    }

    //Obtener notas
    @Test
    void shouldReturnNotesOfRoom() {

        Calendar calendar = new Calendar();

        calendar.addNote(
                new Note(
                        "Nota",
                        "Contenido",
                        "Lucas"));

        Room room = new Room("Sala", "Valen");
        setField(room,"id","ROOM-123");
        room.setCalendar(calendar);

        when(roomRepository.findByRoomId("ROOM-123"))
                .thenReturn(Optional.of(room));
        assertEquals(1,
                roomService.getNotesByRoom("ROOM-123").size());
    }

    //Un usuario abandona la sala
    @Test
    void shouldLeaveRoomSuccessfully() {
        Room room = new Room("Sala", "Ivan");
        setField(room, "id", "ROOM-123");
        room.getParticipants().add("Lucas");

        when(roomRepository.findByRoomId("ROOM-123"))
                .thenReturn(Optional.of(room));
        roomService.leaveRoom("ROOM-123", "Lucas");
        assertFalse(room.getParticipants().contains("Lucas"));

        verify(roomRepository).save(room);
    }

    /*
    ESCENARIOS IMPROBABLES, agregados por seguridad..
     */

    //Verifica que se lance una excepción cuando un usuario que no pertenece a la sala intenta crear una nota.
    @Test
    void shouldThrowExceptionWhenUserIsNotInRoom() {
        NoteRequest request = new NoteRequest();

        request.setRoomId("ROOM-123");
        request.setUsername("Dyssio");
        request.setNoteTitle("Nota");
        request.setContent("Contenido");

        Room room = new Room("Sala Test", "Valen");
        setField(room, "id", "ROOM-123");

        when(roomRepository.findByRoomId("ROOM-123"))
                .thenReturn(Optional.of(room));
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> roomService.createNote(request));

        assertEquals("El usuario no pertenece a la sala", ex.getMessage());
    }

    //Lanza error en caso de no existir la tarea
    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        TaskRequest request = new TaskRequest();

        request.setRoomId("ROOM-123");
        request.setTaskTitle("No Existe");

        Room room = new Room("Sala", "Valen");
        setField(room, "id", "ROOM-123");

        room.setCalendar(new Calendar());

        when(roomRepository.findByRoomId("ROOM-123")).thenReturn(Optional.of(room));

        assertThrows(RuntimeException.class, () -> roomService.updateTask(request));
    }

    //Verifica que se lance una excepción cuando un usuario ajeno a la sala intenta crear una tarea.
    @Test
    void shouldThrowExceptionWhenCreatingTaskWithoutMembership() {
        TaskRequest request = new TaskRequest();

        request.setRoomId("ROOM-123");
        request.setUsername("Intruso");

        Room room = new Room("Sala", "Valen");
        setField(room, "id", "ROOM-123");

        when(roomRepository.findByRoomId("ROOM-123")).thenReturn(Optional.of(room));

        assertThrows(RuntimeException.class, () -> roomService.createTask(request));
    }

    //Lanzar error en caso de no existir la nota
    @Test
    void shouldThrowExceptionWhenNoteDoesNotExist() {
        NoteRequest request = new NoteRequest();

        request.setRoomId("ROOM-123");
        request.setUsername("Dyssio");
        request.setNoteTitle("Nota Inexistente");

        Room room = new Room("Sala", "Valen");
        setField(room, "id", "ROOM-123");

        room.setCalendar(new Calendar());

        when(roomRepository.findByRoomId("ROOM-123")).thenReturn(Optional.of(room));

        assertThrows(RuntimeException.class, () -> roomService.updateNote(request));
    }

    //Verifica que se lance una excepción cuando se consultan participantes a una sala inexistente.
    @Test
    void shouldThrowExceptionWhenRoomNotFound() {
        when(roomRepository.findByRoomId("ROOM-123")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> roomService.getRoomParticipants("ROOM-123"));
    }

    //Verifica que se lance una excepción cuando el admin expulsa a un usuario inexistente
    @Test
    void shouldThrowExceptionWhenParticipantDoesNotExist() {

        RemoveParticipantRequest request =
                new RemoveParticipantRequest();

        request.setRoomId("ROOM-123");
        request.setAdminName("Valen");
        request.setUsernameToRemove("Lucas");

        Room room =
                new Room("Sala", "Valen");

        setField(room, "id", "ROOM-123");

        when(roomRepository.findByRoomId("ROOM-123"))
                .thenReturn(Optional.of(room));

        assertThrows(
                RuntimeException.class,
                () -> roomService.removeParticipant(request)
        );
    }

    //Verifica que se lance una excepción cuando se intenta completar tarea innexistente
    @Test
    void shouldThrowExceptionWhenCompletingNonExistingTask() {
        TaskRequest request = new TaskRequest();

        request.setRoomId("ROOM-123");
        request.setTaskTitle("No existe");

        Room room = new Room("Sala", "Valen");

        setField(room, "id", "ROOM-123");

        room.setCalendar(new Calendar());

        when(roomRepository.findByRoomId("ROOM-123")).thenReturn(Optional.of(room));

        assertThrows(RuntimeException.class, () -> roomService.completeTask(request));
    }

    //Verifica que se lance una excepción si un usuario inexistente intenta abandonar la sala
    @Test
    void shouldThrowExceptionWhenUserLeavingDoesNotExist() {

        Room room = new Room("Sala", "Valen");
        setField(room, "id", "ROOM-123");

        when(roomRepository.findByRoomId("ROOM-123"))
                .thenReturn(Optional.of(room));

        assertThrows(RuntimeException.class,
                () -> roomService.leaveRoom("ROOM-123", "Lucas"));
    }
}
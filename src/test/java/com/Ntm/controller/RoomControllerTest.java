package com.Ntm.controller;

import com.Ntm.dto.*;
import com.Ntm.service.RoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @Autowired
    private ObjectMapper objectMapper;

    //Test de prueba controller, creacion de sala

    @Test
    void shouldCreateRoomSuccessfully() throws Exception {
        CreateRoomRequest request = new CreateRoomRequest();
        CreateRoomResponse response = mock(CreateRoomResponse.class);

        when(roomService.createRoom(any(CreateRoomRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    //Test de prueba controller, usuario se une a una sala
    @Test
    void shouldJoinRoomSuccessfully() throws Exception {
        JoinRoomRequest request = new JoinRoomRequest();
        JoinRoomResponse response = mock(JoinRoomResponse.class);

        when(roomService.joinRoom(any(JoinRoomRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/rooms/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    //Test de prueba controller, eliminar sala

    @Test
    void shouldDeleteRoomSuccessfully() throws Exception {
        String roomId = "ROOM-123";
        DeleteRoomRequest request = new DeleteRoomRequest();
        DeleteRoomResponse response = mock(DeleteRoomResponse.class);

        when(roomService.deleteRoom(eq(roomId), any(DeleteRoomRequest.class))).thenReturn(response);

        mockMvc.perform(delete("/api/rooms/{roomId}", roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    //Test de prueba controller, acceso masterkey admin

    @Test
    void shouldValidateMasterKeySuccessfully() throws Exception {
        AdminAccessRequest request = new AdminAccessRequest();
        AdminAccessResponse response = new AdminAccessResponse();

        when(roomService.grantAdminAccess(any(AdminAccessRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/rooms/validate-masterkey")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    //Test de prueba controller, eliminar usuario

    @Test
    void shouldRemoveParticipantSuccessfully() throws Exception {
        RemoveParticipantRequest request = new RemoveParticipantRequest();

        mockMvc.perform(post("/api/rooms/remove-participant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario eliminado de la sala exitosamente"));
    }

    @Test
    void shouldReturnBadRequestWhenRemovingNonExistentUser() throws Exception {
        RemoveParticipantRequest request = new RemoveParticipantRequest();

        doThrow(new RuntimeException("Usuario inexistente en la sala"))
                .when(roomService).removeParticipant(any(RemoveParticipantRequest.class));

        mockMvc.perform(post("/api/rooms/remove-participant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Usuario inexistente en la sala"));
    }

    //Test de prueba RoomController, Crear tarea
    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        TaskRequest request = new TaskRequest();

        mockMvc.perform(post("/api/rooms/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Tarea creada exitosamente"));

        verify(roomService).createTask(any(TaskRequest.class));
    }
    @Test
    void shouldReturnBadRequestWhenCreateTaskFails() throws Exception {
        TaskRequest request = new TaskRequest();

        doThrow(new RuntimeException("Error al crear tarea"))
                .when(roomService)
                .createTask(any(TaskRequest.class));

        mockMvc.perform(post("/api/rooms/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al crear tarea"));
    }

    //Test de prueba RoomController, Actualizar tarea
    @Test
    void shouldUpdateTaskSuccessfully() throws Exception {
        TaskRequest request = new TaskRequest();

        mockMvc.perform(put("/api/rooms/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Tarea actualizada exitosamente"));

        verify(roomService).updateTask(any(TaskRequest.class));
    }
    @Test
    void shouldReturnBadRequestWhenUpdateTaskFails() throws Exception {
        TaskRequest request = new TaskRequest();

        doThrow(new RuntimeException("Tarea no encontrada"))
                .when(roomService)
                .updateTask(any(TaskRequest.class));

        mockMvc.perform(put("/api/rooms/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Tarea no encontrada"));
    }

    //Test de prueba RoomController, Completar tarea
    @Test
    void shouldCompleteTaskSuccessfully() throws Exception {
        TaskRequest request = new TaskRequest();

        mockMvc.perform(patch("/api/rooms/tasks/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Tarea completada exitosamente"));

        verify(roomService).completeTask(any(TaskRequest.class));
    }
    @Test
    void shouldReturnBadRequestWhenCompleteTaskFails() throws Exception {
        TaskRequest request = new TaskRequest();

        doThrow(new RuntimeException("Tarea no encontrada"))
                .when(roomService)
                .completeTask(any(TaskRequest.class));

        mockMvc.perform(patch("/api/rooms/tasks/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Tarea no encontrada"));
    }

    //Test de prueba RoomController, Crear nota
    @Test
    void shouldCreateNoteSuccessfully() throws Exception {
        NoteRequest request = new NoteRequest();

        mockMvc.perform(post("/api/rooms/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Nota agregada exitosamente"));

        verify(roomService).createNote(any(NoteRequest.class));
    }
    @Test
    void shouldReturnBadRequestWhenCreateNoteFails() throws Exception {
        NoteRequest request = new NoteRequest();

        doThrow(new RuntimeException("Usuario no pertenece a la sala"))
                .when(roomService)
                .createNote(any(NoteRequest.class));

        mockMvc.perform(post("/api/rooms/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Usuario no pertenece a la sala"));
    }

    //Test de prueba RoomController, Actualizar nota
    @Test
    void shouldUpdateNoteSuccessfully() throws Exception {
        NoteRequest request = new NoteRequest();

        mockMvc.perform(put("/api/rooms/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Nota editada exitosamente"));

        verify(roomService).updateNote(any(NoteRequest.class));
    }
    @Test
    void shouldReturnBadRequestWhenUpdateNoteFails() throws Exception {
        NoteRequest request = new NoteRequest();

        doThrow(new RuntimeException("No tienes permisos para editar esta nota"))
                .when(roomService)
                .updateNote(any(NoteRequest.class));

        mockMvc.perform(put("/api/rooms/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No tienes permisos para editar esta nota"));
    }

    //Test de prueba RoomController, Borrar nota
    @Test
    void shouldDeleteNoteSuccessfully() throws Exception {
        NoteRequest request = new NoteRequest();

        mockMvc.perform(delete("/api/rooms/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Nota eliminada exitosamente"));

        verify(roomService).deleteNote(any(NoteRequest.class));
    }
    @Test
    void shouldReturnBadRequestWhenDeleteNoteFails() throws Exception {
        NoteRequest request = new NoteRequest();

        doThrow(new RuntimeException("No tienes permisos para eliminar esta nota"))
                .when(roomService)
                .deleteNote(any(NoteRequest.class));

        mockMvc.perform(delete("/api/rooms/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No tienes permisos para eliminar esta nota"));
    }
}
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
import java.util.ArrayList;
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

    @Test
    void shouldCreateRoomSuccessfully() throws Exception {
        CreateRoomRequest request = new CreateRoomRequest();
        CreateRoomResponse response = new CreateRoomResponse("ROOM-123", "secret123", "Sala de Valen");

        when(roomService.createRoom(any(CreateRoomRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldJoinRoomSuccessfully() throws Exception {
        JoinRoomRequest request = new JoinRoomRequest();
        JoinRoomResponse response = new JoinRoomResponse("ROOM-123", "Sala de Valen", new ArrayList<>());

        when(roomService.joinRoom(any(JoinRoomRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/rooms/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteRoomSuccessfully() throws Exception {
        DeleteRoomRequest request = new DeleteRoomRequest();
        DeleteRoomResponse response = new DeleteRoomResponse("La sala ha sido eliminada exitosamente.");

        when(roomService.deleteRoom(eq("ROOM-123"), any(DeleteRoomRequest.class))).thenReturn(response);

        mockMvc.perform(delete("/api/rooms/ROOM-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldValidateMasterKeySuccessfully() throws Exception {
        AdminAccessRequest request = new AdminAccessRequest();
        AdminAccessResponse response = new AdminAccessResponse();
        response.setRoomId("ROOM-123");
        response.setAdminName("Valen");

        // CAMBIO AQUÍ: de grantAdminAccess a validateMasterKey
        when(roomService.validateMasterKey(any(AdminAccessRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/rooms/validate-masterkey")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRemoveParticipantSuccessfully() throws Exception {
        RemoveParticipantRequest request = new RemoveParticipantRequest();

        mockMvc.perform(post("/api/rooms/remove-participant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Participante eliminado exitosamente de la sala."));
    }

    @Test
    void shouldReturnBadRequestWhenRemovingNonExistentUser() throws Exception {
        RemoveParticipantRequest request = new RemoveParticipantRequest();

        doThrow(new RuntimeException("Usuario inexistente en la sala"))
                .when(roomService)
                .removeParticipant(any(RemoveParticipantRequest.class));

        mockMvc.perform(post("/api/rooms/remove-participant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Usuario inexistente en la sala"));
    }

    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        TaskRequest request = new TaskRequest();

        mockMvc.perform(post("/api/rooms/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Tarea agregada exitosamente"));

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

    @Test
    void shouldCompleteTaskSuccessfully() throws Exception {
        TaskRequest request = new TaskRequest();

        mockMvc.perform(put("/api/rooms/tasks/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Tarea completada/actualizada exitosamente"));

        verify(roomService).completeTask(any(TaskRequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenCompleteTaskFails() throws Exception {
        TaskRequest request = new TaskRequest();

        doThrow(new RuntimeException("Tarea no encontrada"))
                .when(roomService)
                .completeTask(any(TaskRequest.class));

        mockMvc.perform(put("/api/rooms/tasks/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Tarea no encontrada"));
    }

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
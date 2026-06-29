package com.ntm.integration;

import com.ntm.dto.*;
import com.ntm.entity.Room;
import com.ntm.entity.Task;
import com.ntm.repository.RoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.ServletException;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RoomIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ObjectMapper objectMapper;

    //CU1 Crear sala
    @Test
    void shouldCreateRoomEndToEnd() throws Exception {
        CreateRoomRequest request = new CreateRoomRequest();
        request.setRoomName("Sala Integracion");
        request.setAdminName("Ivan");

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomId").exists())
                .andExpect(jsonPath("$.masterKey").exists())
                .andExpect(jsonPath("$.roomName").value("Sala Integracion"));

        assertEquals(1, roomRepository.findAll().size());
    }

    @Test
    void shouldNotCreateRoomWhenNameIsEmpty() throws Exception {
        CreateRoomRequest request = new CreateRoomRequest();
        request.setRoomName("");
        request.setAdminName("Valen");

        assertThrows(ServletException.class, () ->
                mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
        );

        assertEquals(0, roomRepository.count());
    }

    //CU2 Unirse a sala
    @Test
    void shouldJoinRoomEndToEnd() throws Exception {
        Room room = new Room("Sala Test", "Valen");

        roomRepository.save(room);

        JoinRoomRequest request = new JoinRoomRequest();

        request.setRoomId(room.getId());
        request.setUsername("Dyssio");

        mockMvc.perform(
                        post("/api/rooms/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId").value(room.getId()));

        Room updatedRoom = roomRepository.findByRoomId(room.getId()).orElseThrow();

        assertTrue(updatedRoom.getParticipants().contains("Dyssio"));
    }

    @Test
    void shouldNotJoinRoomWithEmptyUsername() throws Exception {
        Room room = new Room("Sala Test", "Valen");
        roomRepository.save(room);

        JoinRoomRequest request = new JoinRoomRequest();
        request.setRoomId(room.getId());
        request.setUsername("");

        assertThrows(Exception.class, () ->
                mockMvc.perform(post("/api/rooms/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
        );

        Room updated = roomRepository.findByRoomId(room.getId()).orElseThrow();
        assertEquals(1, updated.getParticipants().size());
    }

    //CU3 Disolver sala
    @Test
    void shouldDeleteRoomEndToEnd() throws Exception {
        Room room = new Room("Sala Test", "Valen");

        roomRepository.save(room);

        DeleteRoomRequest request =
                new DeleteRoomRequest();

        request.setAdminName("Valen");

        mockMvc.perform(delete("/api/rooms/{roomId}", room.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        assertTrue(roomRepository.findByRoomId(room.getId()).isEmpty());
    }

    @Test
    void shouldNotDeleteRoomWhenRequesterIsNotAdmin() throws Exception {

        Room room = new Room("Sala", "Ivan");
        roomRepository.save(room);

        DeleteRoomRequest request = new DeleteRoomRequest();
        request.setAdminName("Dyssio");

        assertThrows(ServletException.class, () ->
                mockMvc.perform(delete("/api/rooms/{roomId}", room.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
        );

        assertTrue(roomRepository.findByRoomId(room.getId()).isPresent());
    }

    //CU4 Acceder mediante masterKey
    @Test
    void shouldGrantAdminAccessEndToEnd() throws Exception {
        Room room = new Room("Sala Test", "Valen");

        roomRepository.save(room);

        AdminAccessRequest request = new AdminAccessRequest();

        request.setMasterKey(room.getMasterKey());

        mockMvc.perform(post("/api/rooms/validate-masterkey")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId").value(room.getId()))
                .andExpect(jsonPath("$.adminName").value("Valen"));
    }

    @Test
    void shouldRejectInvalidMasterKey() throws Exception {
        AdminAccessRequest request = new AdminAccessRequest();
        request.setMasterKey("incorrecta");

        assertThrows(ServletException.class, () ->
                mockMvc.perform(post("/api/rooms/validate-masterkey")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
        );
    }

    //CU5 Gestionar notas o avisos
    @Test
    void shouldManageNoteEndToEnd() throws Exception {
        CreateRoomRequest roomRequest = new CreateRoomRequest();
        roomRequest.setRoomName("Sala Notas");
        roomRequest.setAdminName("Valen");

        String roomResponse = mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        CreateRoomResponse roomData = objectMapper.readValue(roomResponse, CreateRoomResponse.class);
        String roomId = roomData.getRoomId();

        NoteRequest noteRequest = new NoteRequest();
        noteRequest.setRoomId(roomId);
        noteRequest.setUsername("Valen");
        noteRequest.setNoteTitle("Nota de prueba");
        noteRequest.setContent("Contenido de la nota");

        mockMvc.perform(post("/api/rooms/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Nota agregada exitosamente"));

        Room room = roomRepository.findByRoomId(roomId).orElseThrow();
        assertEquals(1, room.getCalendar().getNotes().size());

        noteRequest.setContent("Contenido actualizado");
        mockMvc.perform(put("/api/rooms/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Nota editada exitosamente"));
        room = roomRepository.findByRoomId(roomId).orElseThrow();
        assertEquals("Contenido actualizado", room.getCalendar().getNotes().get(0).getContent());

        mockMvc.perform(delete("/api/rooms/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Nota eliminada exitosamente"));
        room = roomRepository.findByRoomId(roomId).orElseThrow();
        assertTrue(room.getCalendar().getNotes().isEmpty());
    }

    @Test
    void shouldNotEditNoteCreatedByAnotherUser() throws Exception {
        Room room = new Room("Sala", "Ivan");
        room.addParticipant("Dyssio");
        room.addParticipant("Valen");
        roomRepository.save(room);

        NoteRequest request = new NoteRequest();
        request.setRoomId(room.getId());
        request.setUsername("Dyssio");
        request.setNoteTitle("Nota");
        request.setContent("Contenido");

        mockMvc.perform(post("/api/rooms/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        request.setUsername("Valen");
        request.setContent("Nuevo");

        mockMvc.perform(put("/api/rooms/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    //CU6 Recibir notificaciones
    @Test
    void shouldCreateNotificationWhenTaskIsOverdueEndToEnd() throws Exception {
        CreateRoomRequest roomRequest = new CreateRoomRequest();
        roomRequest.setRoomName("Sala Notificaciones");
        roomRequest.setAdminName("Ivan");

        String roomResponse = mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        CreateRoomResponse roomData = objectMapper.readValue(roomResponse, CreateRoomResponse.class);
        String roomId = roomData.getRoomId();

        JoinRoomRequest joinRequest = new JoinRoomRequest();
        joinRequest.setRoomId(roomId);
        joinRequest.setUsername("Lucas");

        mockMvc.perform(post("/api/rooms/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinRequest)))
                .andExpect(status().isOk());

        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setRoomId(roomId);
        taskRequest.setUsername("Ivan");
        taskRequest.setTaskTitle("Tarea atrasada");
        taskRequest.setDescription("Esta tarea esta atrasada");
        taskRequest.setDueDate(new Date(System.currentTimeMillis() - 86400000));

        mockMvc.perform(post("/api/rooms/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Tarea agregada exitosamente"));

        Room room = roomRepository.findByRoomId(roomId).orElseThrow();
        Task task = room.getCalendar().getTasks().get(0);
        assertTrue(task.isOverdue());

        mockMvc.perform(get("/notifications/" + roomId + "/Lucas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("La tarea 'Tarea atrasada' está atrasada"))
                .andExpect(jsonPath("$[0].targetUser").value("Lucas"))
                .andExpect(jsonPath("$[0].roomMasterKey").value(roomId));
    }

    @Test
    void shouldNotCreateNotificationWhenTaskIsNotOverdue() throws Exception {
        CreateRoomRequest roomRequest = new CreateRoomRequest();
        roomRequest.setRoomName("Sala");
        roomRequest.setAdminName("Ivan");

        String json = mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        CreateRoomResponse room =
                objectMapper.readValue(json, CreateRoomResponse.class);

        TaskRequest task = new TaskRequest();
        task.setRoomId(room.getRoomId());
        task.setUsername("Ivan");
        task.setTaskTitle("Futura");
        task.setDescription("...");
        task.setDueDate(new Date(System.currentTimeMillis()+86400000));

        mockMvc.perform(post("/api/rooms/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/notifications/" + room.getRoomId() + "/Valen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    //CU7 Eliminar usuario por admin
    @Test
    void shouldRemoveParticipantEndToEnd() throws Exception {
        Room room = new Room("Sala Test", "Valen");

        room.addParticipant("Dyssio");

        roomRepository.save(room);

        RemoveParticipantRequest request = new RemoveParticipantRequest();

        request.setRoomId(room.getId());
        request.setAdminName("Valen");
        request.setUsernameToRemove("Dyssio");

        mockMvc.perform(post("/api/rooms/remove-participant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Room updatedRoom = roomRepository.findByRoomId(room.getId()).orElseThrow();

        assertFalse(updatedRoom.getParticipants().contains("Dyssio"));
    }

    @Test
    void shouldNotAllowNonAdminToRemoveParticipant() throws Exception {
        Room room = new Room("Sala", "Ivan");
        room.addParticipant("Dyssio");
        roomRepository.save(room);

        RemoveParticipantRequest request =
                new RemoveParticipantRequest();

        request.setRoomId(room.getId());
        request.setAdminName("Dyssio");
        request.setUsernameToRemove("Ivan");

        mockMvc.perform(post("/api/rooms/remove-participant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        Room updated = roomRepository.findByRoomId(room.getId()).orElseThrow();

        assertTrue(updated.getParticipants().contains("Dyssio"));
    }

}
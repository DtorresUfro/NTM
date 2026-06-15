package com.Ntm.integration;

import com.Ntm.dto.*;
import com.Ntm.entity.Room;
import com.Ntm.repository.RoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        request.setAdminName("Valen");

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomId").exists())
                .andExpect(jsonPath("$.masterKey").exists())
                .andExpect(jsonPath("$.roomName").value("Sala Integracion"));

        assertEquals(1, roomRepository.findAll().size());
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
}
package com.Ntm.controller;

import com.Ntm.dto.CreateRoomRequest;
import com.Ntm.dto.CreateRoomResponse;
import com.Ntm.service.RoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoomService roomService;

    @Test
    void shouldReturn201WhenRoomIsCreated() throws Exception {

        CreateRoomResponse response = new CreateRoomResponse(
                "ABCD1234",
                "MK-12345",
                "Sala Test"
        );

        when(roomService.createRoom(any(CreateRoomRequest.class)))
                .thenReturn(response);

        String json = """
                {
                    "roomName": "Sala Test",
                    "adminName": "Valen"
                }
                """;

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomId").value("ABCD1234"))
                .andExpect(jsonPath("$.masterKey").value("MK-12345"))
                .andExpect(jsonPath("$.roomName").value("Sala Test"));
    }
}
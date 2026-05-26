package com.Ntm.controller;

import com.Ntm.dto.AdminAccessRequest;
import com.Ntm.dto.AdminAccessResponse;
import com.Ntm.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(RoomController.class)
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @Test
    public void shouldReturn200AndAdminRoleWhenMasterKeyIsValid() throws Exception {
        // Tu JSON de prueba
        String requestJson = "{\"roomId\":\"SALA-123\",\"masterKey\":\"secret123\",\"userName\":\"Valen\"}";

        mockMvc.perform(post("/validate-masterkey")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }
}
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

/**
 * Habilita el entorno de pruebas enfocado exclusivamente en la capa MVC web.
 * Escanea y configura el controlador especificado sin levantar todo el contexto de Spring.
 */
@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoomService roomService;

    /**
     * Valida que una petición HTTP POST válida devuelva un estado 201 (Created)
     * junto con la estructura JSON correcta en el cuerpo de la respuesta.
     */
    @Test
    void shouldReturn201WhenRoomIsCreated() throws Exception {
        // Arrange: Configuración del escenario de simulación (Mocking)
        CreateRoomResponse response = new CreateRoomResponse(
                "ABCD1234",
                "MK-12345",
                "Sala Test"
        );

        // Define el comportamiento del servicio simulado al recibir cualquier Request DTO
        when(roomService.createRoom(any(CreateRoomRequest.class)))
                .thenReturn(response);

        // Definición de la carga útil (Payload) en formato JSON estructurado
        String json = """
                {
                    "roomName": "Sala Test",
                    "adminName": "Valen"
                }
                """;

        // Act & Assert: Ejecución de la solicitud HTTP simulada y aserciones de salida
        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated()) // Valida código HTTP 201
                .andExpect(jsonPath("$.roomId").value("ABCD1234"))  // Valida nodo del JSON
                .andExpect(jsonPath("$.masterKey").value("MK-12345"))
                .andExpect(jsonPath("$.roomName").value("Sala Test"));
    }
}
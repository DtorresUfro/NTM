package com.ntm.controller;

import com.ntm.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ViewController.class)
class ViewControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RoomService roomService;

    //Test de prueba ViewController, Volver al inicio
    @Test
    void shouldReturnHomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    //Test de prueba ViewController, Crear sala
    @Test
    void shouldReturnCrearSalaPage() throws Exception {
        mockMvc.perform(get("/crear-sala"))
                .andExpect(status().isOk())
                .andExpect(view().name("crear-sala"));
    }
    @Test
    void shouldReturnSalaCreadaPage() throws Exception {
        mockMvc.perform(get("/sala-creada"))
                .andExpect(status().isOk())
                .andExpect(view().name("sala-creada"));
    }

    @Test
    void shouldReturnJoinOptionsPage() throws Exception {
        mockMvc.perform(get("/join-options"))
                .andExpect(status().isOk())
                .andExpect(view().name("join-options"));
    }

    //Test de prueba ViewController, Unirse a la sala
    @Test
    void shouldReturnUnirseSalaPage() throws Exception {
        mockMvc.perform(get("/unirse-sala"))
                .andExpect(status().isOk())
                .andExpect(view().name("unirse-sala"));
    }
    @Test
    void shouldReturnAdminAccessPage() throws Exception {
        mockMvc.perform(get("/admin-access"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-access"));
    }

    //Test de prueba ViewController, Devolver participantes
    @Test
    void shouldReturnRoomPageWithParticipants() throws Exception {
        when(roomService.getRoomParticipants("ROOM-123"))
                .thenReturn(List.of("Valen (Admin)", "Lucas"));

        mockMvc.perform(get("/room/ROOM-123"))
                .andExpect(status().isOk())
                .andExpect(view().name("room"))
                .andExpect(model().attribute("roomId", "ROOM-123"))
                .andExpect(model().attributeExists("participants"));
    }
    @Test
    void shouldReturnRoomPageWhenParticipantLoadingFails() throws Exception {
        when(roomService.getRoomParticipants("ROOM-123"))
                .thenThrow(new RuntimeException("Sala no encontrada"));

        mockMvc.perform(get("/room/ROOM-123"))
                .andExpect(status().isOk())
                .andExpect(view().name("room"))
                .andExpect(model().attribute("roomId", "ROOM-123"))
                .andExpect(model().attributeExists("participants"));
    }
}
package com.ntm.controller;

import com.ntm.entity.Notification;
import com.ntm.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @Test
    void shouldReturnNotificationsSuccessfully() throws Exception {

        Notification notification = new Notification();
        notification.setMessage("Tarea atrasada");

        when(notificationService.getNotifications("ROOM-123", "Lucas"))
                .thenReturn(List.of(notification));

        mockMvc.perform(get("/notifications/ROOM-123/Lucas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message")
                        .value("Tarea atrasada"));

        verify(notificationService)
                .getNotifications("ROOM-123", "Lucas");
    }

    @Test
    void shouldMarkNotificationAsRead() throws Exception {

        mockMvc.perform(put("/notifications/1/read"))
                .andExpect(status().isOk());

        verify(notificationService).read(1L);
    }

}
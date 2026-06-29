package com.ntm.service;

import com.ntm.entity.Task;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleCalendarEventServiceTest {

    @Mock
    private GoogleCalendarAuthService authService;

    @InjectMocks
    private GoogleCalendarEventService eventService;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setTitle("Tarea de prueba");
        task.setDescription("Descripcion de prueba");
        task.setDueDate(new Date());
        task.setCreatedBy("Lucas");
    }

    @Test
    void shouldThrowExceptionWhenNotAuthenticated() throws Exception {
        when(authService.getCalendarService()).thenReturn(null);

        assertThrows(RuntimeException.class, () ->
                eventService.createEventFromTask(task, "calendar-123"));
    }

    @Test
    void shouldReturnPrimaryCalendarId() throws Exception {

        String result =
                eventService.createCalendarForRoom("Sala Test", "Ivan");

        assertEquals("primary", result);
    }

    @Test
    void shouldThrowExceptionWhenCreatingEventWithoutAuthentication() throws Exception {
        when(authService.getCalendarService()).thenReturn(null);

        assertThrows(RuntimeException.class, () ->
                eventService.createEventFromTask(task, "primary"));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithoutAuthentication() throws Exception {

        when(authService.getCalendarService()).thenReturn(null);

        assertThrows(RuntimeException.class, () ->
                eventService.updateEventFromTask(
                        "event123",
                        task,
                        "primary"));
    }

    @Test
    void shouldThrowExceptionWhenDeletingWithoutAuthentication() throws Exception {

        when(authService.getCalendarService()).thenReturn(null);

        assertThrows(RuntimeException.class, () ->
                eventService.deleteEvent(
                        "event123",
                        "primary"));
    }
}
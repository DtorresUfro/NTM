package com.ntm.service;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.ntm.entity.Task;
import com.ntm.exception.GoogleCalendarException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;

@Service
public class GoogleCalendarEventService {

    private final GoogleCalendarAuthService googleAuthService;

    public GoogleCalendarEventService(GoogleCalendarAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    private com.google.api.services.calendar.Calendar getCalendarService() {
        com.google.api.services.calendar.Calendar service = googleAuthService.getCalendarService();
        if (service == null) {
            throw new GoogleCalendarException("Google Calendar no autenticado");
        }
        return service;
    }

    public Event createEventFromTask(Task task, String calendarId) {
        com.google.api.services.calendar.Calendar service = getCalendarService();

        Event event = new Event()
                .setSummary(task.getTitle())
                .setDescription(task.getDescription());

        if (task.getDueDate() != null) {
            Date dueDate = task.getDueDate();
            EventDateTime start = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(dueDate))
                    .setTimeZone(ZoneId.systemDefault().toString());

            event.setStart(start);
            event.setEnd(start);
        }

        try {
            Event created = service.events()
                    .insert(calendarId, event)
                    .execute();

            task.setGoogleEventId(created.getId());
            return created;
        } catch (IOException e) {
            throw new GoogleCalendarException("No se pudo crear el evento en Google Calendar.", e);
        }
    }

    public String getPrimaryCalendarId() {
        return "primary";
    }

    public Event updateEventFromTask(String eventId, Task task, String calendarId) {
        Event event = new Event()
                .setSummary(task.getTitle())
                .setDescription(task.getDescription());

        if (task.getDueDate() != null) {
            EventDateTime dateTime = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(task.getDueDate()))
                    .setTimeZone(ZoneId.systemDefault().toString());

            event.setStart(dateTime);
            event.setEnd(dateTime);
        }

        try {
            return getCalendarService()
                    .events()
                    .update(calendarId, eventId, event)
                    .execute();
        } catch (IOException e) {
            throw new GoogleCalendarException("No se pudo actualizar el evento en Google Calendar.", e);
        }
    }

    public void deleteEvent(String eventId, String calendarId) {
        try {
            getCalendarService()
                    .events()
                    .delete(calendarId, eventId)
                    .execute();
        } catch (IOException e) {
            throw new GoogleCalendarException("No se pudo eliminar el evento en Google Calendar.", e);
        }
    }
}
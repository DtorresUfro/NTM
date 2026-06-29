package com.ntm.service;

import com.ntm.entity.Task;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;

@Service
public class GoogleCalendarEventService {

    private final GoogleCalendarAuthService googleAuthService;

    public GoogleCalendarEventService(GoogleCalendarAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    private com.google.api.services.calendar.Calendar getCalendarService() throws Exception {
        com.google.api.services.calendar.Calendar service = googleAuthService.getCalendarService();
        if (service == null) {
            throw new RuntimeException("Google Calendar no autenticado");
        }
        return service;
    }

    public Event createEventFromTask(Task task, String calendarId) throws Exception {
        com.google.api.services.calendar.Calendar service = getCalendarService();

        Event event = new Event()
                .setSummary(task.getTitle())
                .setDescription(task.getDescription());

        if (task.getDueDate() != null) {
            Date fecha = task.getDueDate();
            EventDateTime start = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(fecha))
                    .setTimeZone(ZoneId.systemDefault().toString());

            event.setStart(start);
            event.setEnd(start);
        }

        Event created = service.events()
                .insert(calendarId, event)
                .execute();

        task.setGoogleEventId(created.getId());
        return created;
    }

    // MODIFICADO: Ya no crea calendarios ajenos en la cuenta de Google
    public String createCalendarForRoom(String roomName, String adminName) throws Exception {
        // Retornamos el ID por defecto del calendario principal del usuario
        return "primary";
    }

    public Event updateEventFromTask(String eventId, Task task, String calendarId) throws Exception {
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

        return getCalendarService()
                .events()
                .update(calendarId, eventId, event)
                .execute();
    }

    public void deleteEvent(String eventId, String calendarId) throws Exception {
        getCalendarService()
                .events()
                .delete(calendarId, eventId)
                .execute();
    }
}
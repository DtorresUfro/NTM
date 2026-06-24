package com.Ntm.service;

import com.Ntm.entity.Task;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class GoogleCalendarEventService {

    private final GoogleCalendarAuthService authService;

    public GoogleCalendarEventService(GoogleCalendarAuthService authService) {
        this.authService = authService;
    }

    /**
     * Obtiene el servicio de Calendar, lanzando excepción si no está autenticado
     */
    private Calendar getCalendarService() throws Exception {
        Calendar service = authService.getCalendarService();
        if (service == null) {
            throw new RuntimeException("No autenticado con Google. Por favor, ve a /auth/google para iniciar sesión.");
        }
        return service;
    }

    public String createCalendarForRoom(String roomName, String adminName) throws Exception {
        Calendar service = getCalendarService();

        com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
        calendar.setSummary("NoteTask - " + roomName);
        calendar.setDescription("Calendario para la sala " + roomName + " (Admin: " + adminName + ")");
        calendar.setTimeZone("America/Santiago");

        com.google.api.services.calendar.model.Calendar createdCalendar = service.calendars().insert(calendar).execute();
        return createdCalendar.getId();
    }

    public Event createEventFromTask(Task task, String calendarId) throws Exception {
        Calendar service = getCalendarService();

        Event event = new Event()
                .setSummary(task.getTitle())
                .setDescription(task.getDescription() + "\n\nCreado por: " + task.getCreatedBy());

        Date dueDate = task.getDueDate();
        DateTime startDateTime = new DateTime(dueDate);
        EventDateTime start = new EventDateTime().setDateTime(startDateTime);
        event.setStart(start);

        DateTime endDateTime = new DateTime(dueDate.getTime() + 3600000);
        EventDateTime end = new EventDateTime().setDateTime(endDateTime);
        event.setEnd(end);

        return service.events().insert(calendarId, event).execute();
    }

    public Event updateEventFromTask(String eventId, Task task, String calendarId) throws Exception {
        Calendar service = getCalendarService();

        Event event = service.events().get(calendarId, eventId).execute();
        event.setSummary(task.getTitle());
        event.setDescription(task.getDescription() + "\n\nActualizado por: " + task.getCreatedBy());

        Date dueDate = task.getDueDate();
        event.setStart(new EventDateTime().setDateTime(new DateTime(dueDate)));
        event.setEnd(new EventDateTime().setDateTime(new DateTime(dueDate.getTime() + 3600000)));

        return service.events().update(calendarId, eventId, event).execute();
    }

    public void deleteEvent(String eventId, String calendarId) throws Exception {
        Calendar service = getCalendarService();
        service.events().delete(calendarId, eventId).execute();
    }
}
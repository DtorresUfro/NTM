package com.Ntm.service;

import com.Ntm.entity.Calendar;
import com.Ntm.entity.Note;
import com.Ntm.entity.Room;
import com.Ntm.entity.Task;
import com.Ntm.exception.InvalidRoomException;
import com.Ntm.exception.RoomNotFoundException;
import com.Ntm.exception.UnauthorizedRoomActionException;
import com.Ntm.dto.*;
import com.Ntm.repository.RoomRepository;
import com.google.api.services.calendar.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final GoogleCalendarEventService googleCalendarService;

    @Autowired
    public RoomService(RoomRepository roomRepository, GoogleCalendarEventService googleCalendarService) {
        this.roomRepository = roomRepository;
        this.googleCalendarService = googleCalendarService;
    }

    public RoomService(RoomRepository roomRepository) {
        this(roomRepository, null);
    }

    // ==========================================
    // MÉTODOS DE GESTIÓN DE SALAS (ROOMS)
    // ==========================================

    public CreateRoomResponse createRoom(CreateRoomRequest request) {
        validateRoomData(request);

        Room room = new Room(request.getRoomName(), request.getAdminName());

        Calendar internalCalendar = new Calendar();
        internalCalendar.setName("Calendario de " + request.getRoomName());
        room.setCalendar(internalCalendar);

        Room savedRoom = roomRepository.save(room);
        return new CreateRoomResponse(savedRoom.getId(), savedRoom.getMasterKey(), savedRoom.getName());
    }

    public JoinRoomResponse joinRoom(JoinRoomRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("La sala no existe o el ID es incorrecto"));

        if (room.getParticipants().contains(request.getUsername()) ||
                request.getUsername().equalsIgnoreCase(room.getAdminName())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso en esta sala.");
        }

        room.getParticipants().add(request.getUsername());
        roomRepository.save(room);

        return new JoinRoomResponse(room.getId(), room.getName(), getRoomParticipants(room.getId()));
    }

    public DeleteRoomResponse deleteRoom(String roomId, DeleteRoomRequest request) {
        Room room = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Sala no encontrada"));

        if (!room.getAdminName().equalsIgnoreCase(request.getAdminName())) {
            throw new UnauthorizedRoomActionException("Solo el administrador puede eliminar esta sala.");
        }

        roomRepository.delete(room);
        return new DeleteRoomResponse("La sala ha sido eliminada exitosamente.");
    }

    // ==========================================
    // ACCESO DE ADMINISTRADOR (Master Key)
    // ==========================================

    public AdminAccessResponse validateMasterKey(AdminAccessRequest request) {
        if (request.getMasterKey() == null || request.getMasterKey().trim().isEmpty()) {
            throw new RuntimeException("La Master Key es obligatoria");
        }

        // Buscamos la sala asociada a esa llave única
        Room room = roomRepository.findByMasterKey(request.getMasterKey())
                .orElseThrow(() -> new RuntimeException("La Llave Maestra (Master Key) proporcionada es incorrecta o la sala no existe."));

        AdminAccessResponse response = new AdminAccessResponse();
        response.setRoomId(room.getId());
        response.setAdminName(room.getAdminName());
        return response;
    }

    public void removeParticipant(RemoveParticipantRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Sala no encontrada"));

        if (!room.getAdminName().equalsIgnoreCase(request.getAdminName())) {
            throw new UnauthorizedRoomActionException("Acción denegada: Solo el administrador puede remover participantes.");
        }

        if (request.getUsernameToRemove().equalsIgnoreCase(room.getAdminName())) {
            throw new IllegalArgumentException("No se puede remover al administrador de la sala.");
        }

        boolean removed = room.getParticipants().removeIf(p -> p.equalsIgnoreCase(request.getUsernameToRemove()));
        if (!removed) {
            throw new IllegalArgumentException("El participante especificado no se encuentra en la sala.");
        }

        roomRepository.save(room);
    }

    public List<String> getRoomParticipants(String roomId) {
        Room room = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RoomNotFoundException("La sala no existe"));

        List<String> allMembers = new ArrayList<>();

        if (room.getAdminName() != null && !room.getAdminName().trim().isEmpty()) {
            allMembers.add(room.getAdminName() + " (Admin)");
        }

        if (room.getParticipants() != null) {
            for (String participant : room.getParticipants()) {
                if (!participant.equalsIgnoreCase(room.getAdminName())) {
                    allMembers.add(participant);
                }
            }
        }
        return allMembers;
    }

    // ==========================================
    // MÉTODOS DE GESTIÓN DE TAREAS (TASKS)
    // ==========================================

    public List<Task> getTasksByRoom(String roomId) {
        Room room = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Sala no encontrada"));

        if (room.getCalendar() == null) {
            return new ArrayList<>();
        }

        return room.getCalendar().getTasks();
    }

    public void createTask(TaskRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Sala no encontrada"));

        if (room.getCalendar() == null) {
            throw new RuntimeException("La sala no tiene un calendario asociado.");
        }

        Task task = new Task();
        task.setRoomMasterKey(room.getMasterKey());
        task.setTitle(request.getTaskTitle());
        task.setDescription(request.getDescription());
        task.setStartDate(request.getStartDate() != null ? request.getStartDate() : new Date());
        task.setDueDate(request.getDueDate());
        task.setCreatedBy(request.getUsername());
        task.setCreatedAt(new Date());
        task.setCompleted(false);
        task.setCalendar(room.getCalendar());

        if (googleCalendarService != null && room.getGoogleCalendarId() != null) {
            try {
                Event event = googleCalendarService.createEventFromTask(task, room.getGoogleCalendarId());
                task.setGoogleEventId(event.getId());
            } catch (Exception e) {
                System.err.println("Error al sincronizar tarea con Google Calendar: " + e.getMessage());
            }
        }

        room.getCalendar().getTasks().add(task);
        roomRepository.save(room);
    }

    public void completeTask(TaskRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Sala no encontrada"));

        if (room.getCalendar() == null) {
            throw new RuntimeException("La sala no tiene un calendario asociado.");
        }

        Task task = room.getCalendar().getTasks().stream()
                .filter(t -> t.getId().equals(request.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada en la sala"));

        // Se cambia el estado al opuesto actual (toggle) o según tu lógica de interacción
        task.setCompleted(!task.isCompleted());

        if (googleCalendarService != null && room.getGoogleCalendarId() != null && task.getGoogleEventId() != null) {
            try {
                googleCalendarService.updateEventFromTask(task.getGoogleEventId(), task, room.getGoogleCalendarId());
                System.out.println("Tarea Actualizada como Completada en Google Calendar");
            } catch (Exception e) {
                System.err.println("Error al actualizar la tarea: " + e.getMessage());
            }
        }

        roomRepository.save(room);
    }

    // ==========================================
    // ACTUALIZAR TAREA
    // ==========================================
    public void updateTask(TaskRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Sala no encontrada"));

        Task task = room.getCalendar().getTasks().stream()
                .filter(t -> t.getTitle().equalsIgnoreCase(request.getTaskTitle()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getStartDate() != null) {
            task.setStartDate(request.getStartDate());
        }

        roomRepository.save(room);
    }

    // ==========================================
    // MÉTODOS DE GESTIÓN DE NOTAS (NOTES)
    // ==========================================

    public List<Note> getNotesByRoom(String roomId) {
        Room room = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Sala no encontrada"));

        if (room.getCalendar() == null) {
            return new ArrayList<>();
        }

        return room.getCalendar().getNotes();
    }

    public void createNote(NoteRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Sala no encontrada"));

        if (room.getCalendar() == null) {
            throw new RuntimeException("La sala no tiene un calendario asociado.");
        }

        Note note = new Note(request.getNoteTitle(), request.getContent(), request.getUsername());
        note.setRoomMasterKey(room.getMasterKey());
        note.setCalendar(room.getCalendar());

        room.getCalendar().getNotes().add(note);
        roomRepository.save(room);
    }

    public void updateNote(NoteRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Sala no encontrada"));

        Note note = room.getCalendar().getNotes().stream()
                .filter(n -> n.getTitle().equalsIgnoreCase(request.getNoteTitle()) && n.isActive())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nota no encontrada o inactiva"));

        note.editContent(request.getContent());
        roomRepository.save(room);
    }

    public void deleteNote(NoteRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Sala no encontrada"));

        Note note = room.getCalendar().getNotes().stream()
                .filter(n -> n.getTitle().equalsIgnoreCase(request.getNoteTitle()) && n.isActive())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nota no encontrada"));

        note.delete();
        roomRepository.save(room);
    }

    // ==========================================
    // VALIDACIONES PRIVADAS
    // ==========================================

    private void validateRoomData(CreateRoomRequest request) {
        if (request.getRoomName() == null || request.getRoomName().trim().isEmpty()) {
            throw new InvalidRoomException("El nombre de la sala es obligatorio");
        }
        if (request.getAdminName() == null || request.getAdminName().trim().isEmpty()) {
            throw new InvalidRoomException("El nombre del administrador es obligatorio");
        }
    }

    // ==========================================
    // ACCESO DE ADMINISTRADOR (Master Key)
    // ==========================================
    public AdminAccessResponse accessWithMasterKey(AdminAccessRequest request) {
        // 1. Validar que la llave maestra no sea nula o vacía
        if (request.getMasterKey() == null || request.getMasterKey().trim().isEmpty()) {
            throw new RuntimeException("La Master Key es obligatoria");
        }

        // 2. Buscar la sala utilizando el método existente en tu RoomRepository
        Room room = roomRepository.findByMasterKey(request.getMasterKey())
                .orElseThrow(() -> new RuntimeException("La Llave Maestra (Master Key) proporcionada es incorrecta o la sala no existe."));

        // 3. Mapear y construir la respuesta con los datos de la sala encontrada
        AdminAccessResponse response = new AdminAccessResponse();
        response.setRoomId(room.getId());
        response.setAdminName(room.getAdminName());

        return response;
    }
}
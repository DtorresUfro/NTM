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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final GoogleCalendarEventService googleCalendarService;

    public RoomService(RoomRepository roomRepository, GoogleCalendarEventService googleCalendarService) {
        this.roomRepository = roomRepository;
        this.googleCalendarService = googleCalendarService;
    }

    private void validateRoomData(CreateRoomRequest request) {
        if (request.getRoomName() == null ||
                request.getRoomName().trim().isEmpty()) {
            throw new InvalidRoomException("El nombre de la sala es obligatorio");
        }
        if (request.getAdminName() == null ||
                request.getAdminName().trim().isEmpty()) {
            throw new InvalidRoomException("El nombre del administrador es obligatorio");
        }
    }

    public CreateRoomResponse createRoom(CreateRoomRequest request) {
        validateRoomData(request);
        Room room = new Room(
                request.getRoomName(),
                request.getAdminName()
        );
        roomRepository.save(room);
        return new CreateRoomResponse(
                room.getId(),
                room.getMasterKey(),
                room.getName()
        );
    }

    private void validateJoinRequest(JoinRoomRequest request) {
        if (request.getRoomId() == null ||
                request.getRoomId().trim().isEmpty()) {
            throw new InvalidRoomException(
                    "El ID de la sala es obligatorio");
        }
        if (request.getUsername() == null ||
                request.getUsername().trim().isEmpty()) {
            throw new InvalidRoomException(
                    "El nombre de usuario es obligatorio");
        }
    }

    public JoinRoomResponse joinRoom(JoinRoomRequest request) {
        validateJoinRequest(request);
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() ->
                        new RoomNotFoundException("La sala indicada no existe"));
        if (room.getParticipants().contains(request.getUsername())) {
            throw new InvalidRoomException(
                    "El nombre ya está en uso en esta sala");
        }
        room.addParticipant(request.getUsername());
        roomRepository.save(room);
        return new JoinRoomResponse(
                room.getId(),
                room.getName(),
                room.getParticipants()
        );
    }

    private void validateDeleteRequest(DeleteRoomRequest request) {
        if (request.getAdminName() == null ||
                request.getAdminName().trim().isEmpty()) {
            throw new InvalidRoomException(
                    "El nombre del administrador es obligatorio");
        }
    }

    public DeleteRoomResponse deleteRoom(String roomId, DeleteRoomRequest request) {
        validateDeleteRequest(request);
        Room room = roomRepository.findByRoomId(roomId)
                .orElseThrow(() ->
                        new RoomNotFoundException("La sala no existe"));
        if (!room.getAdminName().equals(request.getAdminName())) {
            throw new UnauthorizedRoomActionException(
                    "Solo el administrador puede eliminar la sala");
        }
        roomRepository.delete(room);
        return new DeleteRoomResponse(
                "Sala eliminada correctamente");
    }

    // CORREGIDO: Devuelve el nombre real del Administrador guardado en la BD
    public AdminAccessResponse grantAdminAccess(AdminAccessRequest request) {
        Room room = roomRepository.findByMasterKey(request.getMasterKey())
                .orElseThrow(() -> new IllegalArgumentException("La Master Key ingresada no pertenece a ninguna sala existente"));

        return new AdminAccessResponse(room.getId(), room.getAdminName());
    }

    public void removeParticipant(RemoveParticipantRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));

        if (!room.getAdminName().equals(request.getAdminName())) {
            throw new RuntimeException("Acceso denegado: Solo el administrador puede eliminar usuarios");
        }

        if (!room.getParticipants().contains(request.getUsernameToRemove())) {
            throw new RuntimeException("Usuario inexistente en la sala");
        }

        room.getParticipants().remove(request.getUsernameToRemove());
        roomRepository.save(room);
    }

    // ==========================================
    // SECCIÓN DE GESTIÓN DE NOTAS
    // ==========================================

    public void createNote(NoteRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));

        if (!room.getParticipants().contains(request.getUsername()) && !room.getAdminName().equals(request.getUsername())) {
            throw new RuntimeException("El usuario no pertenece a la sala");
        }

        Note newNote = new Note(request.getNoteTitle(), request.getContent(), request.getUsername());
        newNote.setRoomMasterKey(room.getMasterKey());

        if (room.getCalendar() == null) {
            room.setCalendar(new Calendar());
        }

        room.getCalendar().addNote(newNote);
        roomRepository.save(room);
    }

    public void updateNote(NoteRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));

        Note note = room.getCalendar().getNotes().stream()
                .filter(n -> n.getTitle().equals(request.getNoteTitle()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nota no encontrada"));

        if (!note.getCreatedBy().equals(request.getUsername())) {
            throw new RuntimeException("No tienes permisos para editar esta nota");
        }

        note.editContent(request.getContent());
        roomRepository.save(room);
    }

    public void deleteNote(NoteRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));

        Note note = room.getCalendar().getNotes().stream()
                .filter(n -> n.getTitle().equals(request.getNoteTitle()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nota no encontrada"));

        if (!note.getCreatedBy().equals(request.getUsername())) {
            throw new RuntimeException("No tienes permisos para eliminar esta nota");
        }

        room.getCalendar().getNotes().remove(note);
        roomRepository.save(room);
    }

    // ==========================================
    // SECCIÓN DE GESTIÓN DE TAREAS (TASKS)
    // ==========================================

    public void createTask(TaskRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));

        if (!room.getParticipants().contains(request.getUsername()) && !room.getAdminName().equals(request.getUsername())) {
            throw new RuntimeException("El usuario no pertenece a la sala");
        }

        Task newTask = new Task(
                request.getTaskTitle(),
                request.getDescription(),
                request.getDueDate(),
                request.getUsername(),
                new Date(),
                false
        );
        newTask.setRoomMasterKey(room.getMasterKey());
        newTask.setStartDate(new Date());

        if (room.getGoogleCalendarId() != null) {
            try {
                Event event =
                        googleCalendarService.createEventFromTask(newTask, room.getGoogleCalendarId());
                newTask.setGoogleEventId(event.getId());
                System.out.println("Tarea creada" + event.getId());
            } catch (Exception e) {
                System.err.println("Error al crear la tarea en el Calendario: " + e.getMessage());
            }
        } else {
            System.out.println("La sala no tiene calendario, no se creará la tarea.");
        }

        if (room.getCalendar() == null) {
            room.setCalendar(new Calendar());
        }

        room.getCalendar().addTask(newTask);
        roomRepository.save(room);
    }

    public void updateTask(TaskRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));

        Task task = room.getCalendar().getTasks().stream()
                .filter(t -> t.getTitle().equals(request.getTaskTitle()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        String oldEventId = task.getGoogleEventId();

        task.edit(request.getTaskTitle(), request.getDescription(), request.getDueDate());

        if (oldEventId != null && room.getGoogleCalendarId() != null) {
            try {
                googleCalendarService.updateEventFromTask(oldEventId, task, room.getGoogleCalendarId());
            } catch (Exception e) {
                System.err.println("Hubo un error al actualizar la tarea: " + e.getMessage());
            }
        }

        roomRepository.save(room);
    }

    public void deleteTask(TaskRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));

        Task task = room.getCalendar().getTasks().stream()
                .filter(t -> t.getTitle().equals(request.getTaskTitle()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        if (task.getGoogleEventId() != null && room.getGoogleCalendarId() != null) {
            try {
                googleCalendarService.deleteEvent(task.getGoogleEventId(), room.getGoogleCalendarId());
            } catch (Exception e) {
                System.err.println("Error al eliminar la tarea en el Calendario: " + e.getMessage());
            }
        }
        room.getCalendar().getTasks().remove(task);
        roomRepository.save(room);
    }

    public void completeTask(TaskRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));

        Task task = room.getCalendar().getTasks().stream()
                .filter(t -> t.getTitle().equals(request.getTaskTitle()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        task.complete();
        roomRepository.save(room);

        if (task.getGoogleEventId() != null && room.getGoogleCalendarId() != null) {
            try {
                googleCalendarService.updateEventFromTask(task.getGoogleEventId(), task, room.getGoogleCalendarId());
                System.out.println("Tarea Actualizada como Completada");
            } catch (Exception e) {
                System.err.println("Error al actualizar la tarea: " + e.getMessage());
            }
        }
    }

    public List<Task> getTasksByRoom(String roomId) {
        Room room = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));

        if (room.getCalendar() == null) {
            return new ArrayList<>();
        }

        return room.getCalendar().getTasks();
    }

    // ==========================================
    // SECCIÓN DE CONSULTA DE MIEMBROS
    // ==========================================
    public java.util.List<String> getRoomParticipants(String roomId) {
        Room room = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RoomNotFoundException("La sala no existe"));

        java.util.List<String> allMembers = new java.util.ArrayList<>();

        // 1. Agregamos al administrador original de la base de datos
        if (room.getAdminName() != null && !room.getAdminName().trim().isEmpty()) {
            allMembers.add(room.getAdminName() + " (Admin)");
        }

        // 2. Agregamos al resto de participantes si existen y no están repetidos
        if (room.getParticipants() != null) {
            for (String participant : room.getParticipants()) {
                if (!participant.equalsIgnoreCase(room.getAdminName())) {
                    allMembers.add(participant);
                }
            }
        }
        return allMembers;
    }
}
package com.ntm.service;

import com.ntm.entity.Calendar;
import com.ntm.entity.Note;
import com.ntm.entity.Room;
import com.ntm.entity.Task;
import com.ntm.exception.GoogleCalendarException;
import com.ntm.exception.InvalidMasterKeyException;
import com.ntm.exception.InvalidRequestException;
import com.ntm.exception.InvalidRoomException;
import com.ntm.exception.RoomNotFoundException;
import com.ntm.exception.UnauthorizedRoomActionException;
import com.ntm.dto.*;
import com.ntm.repository.RoomRepository;
import com.google.api.services.calendar.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class RoomService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomService.class);
    private static final String REQUIRED_USER = "El usuario es obligatorio";
    private static final String ROOM_NOT_FOUND = "Sala no encontrada";

    private final RoomRepository roomRepository;
    private final GoogleCalendarEventService googleCalendarService;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    public RoomService(RoomRepository roomRepository,
                       GoogleCalendarEventService googleCalendarService,
                       NotificationService notificationService,
                       ObjectProvider<SimpMessagingTemplate> messagingTemplateProvider) {
        this.roomRepository = roomRepository;
        this.googleCalendarService = googleCalendarService;
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplateProvider == null ? null : messagingTemplateProvider.getIfAvailable();
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

        String normalizedUsername = normalizeRequiredText(request.getUsername(), REQUIRED_USER);

        if (isActiveParticipant(room, normalizedUsername) ||
                normalizedUsername.equalsIgnoreCase(room.getAdminName())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso en esta sala.");
        }

        room.getDisconnectedParticipants().removeIf(participant -> participant.equalsIgnoreCase(normalizedUsername));
        room.getParticipants().add(normalizedUsername);
        roomRepository.save(room);
        publishRoomUpdate(room.getId());

        return new JoinRoomResponse(room.getId(), room.getName(), getRoomParticipants(room.getId()));
    }

    public DeleteRoomResponse deleteRoom(String roomId, DeleteRoomRequest request) {
        Room room = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RoomNotFoundException(ROOM_NOT_FOUND));

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
            throw new InvalidMasterKeyException("La Master Key es obligatoria");
        }

        // Buscamos la sala asociada a esa llave única
        Room room = roomRepository.findByMasterKey(request.getMasterKey())
                .orElseThrow(() -> new InvalidMasterKeyException("La Llave Maestra (Master Key) proporcionada es incorrecta o la sala no existe."));

        AdminAccessResponse response = new AdminAccessResponse();
        response.setRoomId(room.getId());
        response.setAdminName(room.getAdminName());

        room.getDisconnectedParticipants().removeIf(participant -> participant.equalsIgnoreCase(room.getAdminName()));
        roomRepository.save(room);
        publishRoomUpdate(room.getId());
        return response;
    }

    public void removeParticipant(RemoveParticipantRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException(ROOM_NOT_FOUND));


        if (!room.getAdminName().equalsIgnoreCase(request.getAdminName())) {
            throw new UnauthorizedRoomActionException("Acción denegada: Solo el administrador puede remover participantes.");
        }

        if (request.getUsernameToRemove().equalsIgnoreCase(room.getAdminName())) {
            throw new IllegalArgumentException("No se puede remover al administrador de la sala.");
        }

        String usernameToRemove = normalizeRequiredText(request.getUsernameToRemove(), REQUIRED_USER);
        boolean removedFromActive = room.getParticipants().removeIf(p -> p.equalsIgnoreCase(usernameToRemove));
        boolean removedFromDisconnected = room.getDisconnectedParticipants().removeIf(participant -> participant.equalsIgnoreCase(usernameToRemove));
        if (!removedFromActive && !removedFromDisconnected) {
            throw new IllegalArgumentException("El participante especificado no se encuentra en la sala.");
        }

        removeUserContent(room, usernameToRemove);

        roomRepository.save(room);
        publishRoomUpdate(room.getId());
    }

    public void markUserConnected(String roomId, String username) {
        Room room = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RoomNotFoundException(ROOM_NOT_FOUND));

        String normalizedUsername = normalizeRequiredText(username, REQUIRED_USER);
        if (normalizedUsername.equalsIgnoreCase(room.getAdminName())) {
            room.getDisconnectedParticipants().removeIf(participant -> participant.equalsIgnoreCase(normalizedUsername));
        } else if (!isActiveParticipant(room, normalizedUsername)) {
            if (!containsIgnoreCase(room.getDisconnectedParticipants(), normalizedUsername)) {
                throw new IllegalArgumentException("El usuario no pertenece a la sala.");
            }
            room.getDisconnectedParticipants().removeIf(participant -> participant.equalsIgnoreCase(normalizedUsername));
            room.getParticipants().add(normalizedUsername);
        }

        roomRepository.save(room);
        publishRoomUpdate(room.getId());
    }


    public void leaveRoom(String roomId, String username) {
        Room room = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RoomNotFoundException(ROOM_NOT_FOUND));

        String normalizedUsername = Objects.requireNonNull(normalizeRequiredText(username, REQUIRED_USER));
        boolean removed = room.getParticipants().removeIf(p -> p.equalsIgnoreCase(normalizedUsername));
        boolean alreadyDisconnected = containsIgnoreCase(room.getDisconnectedParticipants(), normalizedUsername);
        if (!removed && !alreadyDisconnected && !normalizedUsername.equalsIgnoreCase(room.getAdminName())) {
            throw new IllegalArgumentException("El participante especificado no se encuentra en la sala.");
        }

        if (!containsIgnoreCase(room.getDisconnectedParticipants(), normalizedUsername)) {
            room.getDisconnectedParticipants().add(normalizedUsername);
        }

        roomRepository.save(room);
        publishRoomUpdate(room.getId());
    }
    public List<RoomMemberResponse> getRoomMembers(String roomId) {
        Room room = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RoomNotFoundException("La sala no existe"));

        List<RoomMemberResponse> members = new ArrayList<>();

        if (room.getAdminName() != null && !room.getAdminName().trim().isEmpty()) {
            boolean adminConnected = !containsIgnoreCase(room.getDisconnectedParticipants(), room.getAdminName());
            members.add(new RoomMemberResponse(room.getAdminName(), true, adminConnected));
        }

        if (room.getParticipants() != null) {
            for (String participant : room.getParticipants()) {
                if (!participant.equalsIgnoreCase(room.getAdminName())) {
                    members.add(new RoomMemberResponse(participant, false, true));
                }
            }
        }

        if (room.getDisconnectedParticipants() != null) {
            for (String participant : room.getDisconnectedParticipants()) {
                if (!participant.equalsIgnoreCase(room.getAdminName()) && !isActiveParticipant(room, participant)) {
                    members.add(new RoomMemberResponse(participant, false, false));
                }
            }
        }

        members.sort(Comparator
                .comparing(RoomMemberResponse::isConnected).reversed()
                .thenComparing(RoomMemberResponse::isAdmin).reversed()
                .thenComparing(RoomMemberResponse::getUsername, String.CASE_INSENSITIVE_ORDER));
        return members;
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
                .orElseThrow(() -> new RoomNotFoundException(ROOM_NOT_FOUND));

        if (room.getCalendar() == null) {
            return new ArrayList<>();
        }

        return room.getCalendar().getTasks().stream()
                .filter(Task::isActive)
                .toList();
    }

    public void createTask(TaskRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException(ROOM_NOT_FOUND));

        validateParticipant(room, request.getUsername());
        validateRequiredText(request.getTaskTitle(), "El titulo de la tarea es obligatorio");

        Calendar calendar = ensureCalendar(room);

        Task task = new Task();
        task.setRoomMasterKey(room.getMasterKey());
        task.setTitle(request.getTaskTitle().trim());
        task.setDescription(request.getDescription());
        task.setStartDate(request.getStartDate() != null ? request.getStartDate() : new Date());
        task.setDueDate(request.getDueDate());
        task.setCreatedBy(request.getUsername());
        task.setCreatedAt(new Date());
        task.setCompleted(false);
        task.setCalendar(calendar);

        if (googleCalendarService != null && room.getGoogleCalendarId() != null) {
            try {
                Event event = googleCalendarService.createEventFromTask(task, room.getGoogleCalendarId());
                task.setGoogleEventId(event.getId());
            } catch (GoogleCalendarException e) {
                LOGGER.warn("Error al sincronizar tarea con Google Calendar: {}", e.getMessage());
            }
        }

        calendar.getTasks().add(task);
        roomRepository.save(room);
        publishRoomUpdate(room.getId());

        if (task.isOverdue()) {
            notificationService.createTaskNotification(task, room.getId(), task.getCreatedBy());
            notificationService.createTaskNotification(task, room.getId(), room.getAdminName());
            for (String participant : room.getParticipants()) {
                if (!participant.equals(task.getCreatedBy()) && !participant.equals(room.getAdminName())) {
                    notificationService.createTaskNotification(task, room.getId(), participant);
                }
            }
        }
    }

    public void completeTask(TaskRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException(ROOM_NOT_FOUND));

        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            validateParticipant(room, request.getUsername());
        }

        Task task = findActiveTask(room, request);
        task.setCompleted(!task.isCompleted());

        syncTaskUpdate(room, task);
        roomRepository.save(room);
        publishRoomUpdate(room.getId());
    }

    public void updateTask(TaskRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException(ROOM_NOT_FOUND));

        Task task = findActiveTask(room, request);
        validateOwnerOrAdmin(room, task.getCreatedBy(), request.getUsername(), "No tienes permisos para editar esta tarea");

        if (request.getTaskTitle() != null && !request.getTaskTitle().trim().isEmpty()) {
            task.setTitle(request.getTaskTitle().trim());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getStartDate() != null) {
            task.setStartDate(request.getStartDate());
        }

        syncTaskUpdate(room, task);
        roomRepository.save(room);
        publishRoomUpdate(room.getId());
    }

    public void deleteTask(TaskRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException(ROOM_NOT_FOUND));

        Task task = findActiveTask(room, request);
        validateOwnerOrAdmin(room, task.getCreatedBy(), request.getUsername(), "No tienes permisos para eliminar esta tarea");

        task.delete();

        if (googleCalendarService != null && room.getGoogleCalendarId() != null && task.getGoogleEventId() != null) {
            try {
                googleCalendarService.deleteEvent(task.getGoogleEventId(), room.getGoogleCalendarId());
            } catch (GoogleCalendarException e) {
                LOGGER.warn("Error al eliminar la tarea en Google Calendar: {}", e.getMessage());
            }
        }

        roomRepository.save(room);
        publishRoomUpdate(room.getId());
    }

    // ==========================================
    // METODOS DE GESTION DE NOTAS (NOTES)
    // ==========================================

    public List<Note> getNotesByRoom(String roomId) {
        Room room = roomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RoomNotFoundException(ROOM_NOT_FOUND));

        if (room.getCalendar() == null) {
            return new ArrayList<>();
        }

        return room.getCalendar().getNotes().stream()
                .filter(Note::isActive)
                .toList();
    }

    public void createNote(NoteRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException(ROOM_NOT_FOUND));

        validateParticipant(room, request.getUsername());
        validateRequiredText(request.getNoteTitle(), "El titulo de la nota es obligatorio");
        validateRequiredText(request.getContent(), "El contenido de la nota es obligatorio");

        Calendar calendar = ensureCalendar(room);

        Note note = new Note(request.getNoteTitle().trim(), request.getContent(), request.getUsername());
        note.setRoomMasterKey(room.getMasterKey());
        note.setCalendar(calendar);

        calendar.getNotes().add(note);
        roomRepository.save(room);
        publishRoomUpdate(room.getId());
    }

    public void updateNote(NoteRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException(ROOM_NOT_FOUND));

        Note note = findActiveNote(room, request);
        validateOwnerOrAdmin(room, note.getCreatedBy(), request.getUsername(), "No tienes permisos para editar esta nota");

        if (request.getNoteTitle() != null && !request.getNoteTitle().trim().isEmpty()) {
            note.setTitle(request.getNoteTitle().trim());
        }
        if (request.getContent() != null) {
            note.editContent(request.getContent());
        }

        roomRepository.save(room);
        publishRoomUpdate(room.getId());
    }

    public void deleteNote(NoteRequest request) {
        Room room = roomRepository.findByRoomId(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException(ROOM_NOT_FOUND));

        Note note = findActiveNote(room, request);
        validateOwnerOrAdmin(room, note.getCreatedBy(), request.getUsername(), "No tienes permisos para eliminar esta nota");

        room.getCalendar().getNotes().remove(note);
        note.setCalendar(null);
        roomRepository.save(room);
        publishRoomUpdate(room.getId());
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

    private Calendar ensureCalendar(Room room) {
        if (room.getCalendar() == null) {
            Calendar calendar = new Calendar();
            calendar.setName("Calendario de " + room.getName());
            room.setCalendar(calendar);
        }
        return room.getCalendar();
    }

    private void validateParticipant(Room room, String username) {
        String normalizedUsername = normalizeRequiredText(username, REQUIRED_USER);
        if (!isRoomMember(room, normalizedUsername)) {
            throw new InvalidRequestException("El usuario no pertenece a la sala");
        }
    }


    private boolean isActiveParticipant(Room room, String username) {
        return containsIgnoreCase(room.getParticipants(), username);
    }

    private boolean containsIgnoreCase(List<String> values, String value) {
        return values != null && value != null && values.stream()
                .anyMatch(item -> item.equalsIgnoreCase(value.trim()));
    }

    private void removeUserContent(Room room, String username) {
        if (room.getCalendar() == null) {
            return;
        }

        String normalizedUsername = normalizeRequiredText(username, REQUIRED_USER);

        List<Note> notesToRemove = room.getCalendar().getNotes().stream()
                .filter(note -> note.getCreatedBy() != null && note.getCreatedBy().equalsIgnoreCase(normalizedUsername))
                .toList();
        for (Note note : notesToRemove) {
            room.getCalendar().getNotes().remove(note);
            note.setCalendar(null);
        }

        List<Task> tasksToRemove = room.getCalendar().getTasks().stream()
                .filter(task -> task.getCreatedBy() != null && task.getCreatedBy().equalsIgnoreCase(normalizedUsername))
                .toList();
        for (Task task : tasksToRemove) {
            if (googleCalendarService != null && room.getGoogleCalendarId() != null && task.getGoogleEventId() != null) {
                try {
                    googleCalendarService.deleteEvent(task.getGoogleEventId(), room.getGoogleCalendarId());
                } catch (GoogleCalendarException e) {
                    LOGGER.warn("Error al eliminar la tarea en Google Calendar: {}", e.getMessage());
                }
            }
            room.getCalendar().getTasks().remove(task);
            task.setCalendar(null);
        }
    }

    private boolean isRoomMember(Room room, String username) {
        return (room.getAdminName() != null && room.getAdminName().equalsIgnoreCase(username))
                || isActiveParticipant(room, username);
    }

    private void validateOwnerOrAdmin(Room room, String createdBy, String username, String errorMessage) {
        if (username == null || username.trim().isEmpty()) {
            return;
        }
        String normalizedUsername = username.trim();
        boolean isOwner = createdBy != null && createdBy.equalsIgnoreCase(normalizedUsername);
        boolean isAdmin = room.getAdminName() != null && room.getAdminName().equalsIgnoreCase(normalizedUsername);
        if (!isOwner && !isAdmin) {
            throw new InvalidRequestException(errorMessage);
        }
    }

    private void validateRequiredText(String value, String errorMessage) {
        normalizeRequiredText(value, errorMessage);
    }

    private String normalizeRequiredText(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidRequestException(errorMessage);
        }
        return value.trim();
    }

    private Note findActiveNote(Room room, NoteRequest request) {
        if (room.getCalendar() == null) {
            throw new InvalidRequestException("Nota no encontrada");
        }

        return room.getCalendar().getNotes().stream()
                .filter(Note::isActive)
                .filter(note -> matchesNote(note, request))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("Nota no encontrada"));
    }

    private boolean matchesNote(Note note, NoteRequest request) {
        if (request.getId() != null) {
            return Objects.equals(note.getId(), request.getId());
        }
        return request.getNoteTitle() != null && note.getTitle().equalsIgnoreCase(request.getNoteTitle());
    }

    private Task findActiveTask(Room room, TaskRequest request) {
        if (room.getCalendar() == null) {
            throw new InvalidRequestException("Tarea no encontrada en la sala");
        }

        return room.getCalendar().getTasks().stream()
                .filter(Task::isActive)
                .filter(task -> matchesTask(task, request))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("Tarea no encontrada en la sala"));
    }

    private boolean matchesTask(Task task, TaskRequest request) {
        if (request.getId() != null) {
            return Objects.equals(task.getId(), request.getId());
        }
        return request.getTaskTitle() != null && task.getTitle().equalsIgnoreCase(request.getTaskTitle());
    }

    private void syncTaskUpdate(Room room, Task task) {
        if (googleCalendarService != null && room.getGoogleCalendarId() != null && task.getGoogleEventId() != null) {
            try {
                googleCalendarService.updateEventFromTask(task.getGoogleEventId(), task, room.getGoogleCalendarId());
            } catch (GoogleCalendarException e) {
                LOGGER.warn("Error al actualizar la tarea en Google Calendar: {}", e.getMessage());
            }
        }
    }


    private void publishRoomUpdate(String roomId) {
        if (messagingTemplate != null && roomId != null) {
            messagingTemplate.convertAndSend("/topic/rooms/" + roomId, "updated");
        }
    }

    // ==========================================
    // ACCESO DE ADMINISTRADOR (Master Key)
    // ==========================================
    public AdminAccessResponse accessWithMasterKey(AdminAccessRequest request) {
        return validateMasterKey(request);
    }
}

package com.Ntm.service;

import com.Ntm.dto.*;
import com.Ntm.entity.*;
import com.Ntm.exception.*;
import com.Ntm.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
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
        Room room = roomRepository.findById(request.getRoomId())
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
        Room room = roomRepository.findById(roomId)
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

    public AdminAccessResponse grantAdminAccess(AdminAccessRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("La sala especificada no existe"));

        if (!room.getMasterKey().equals(request.getMasterKey())) {
            throw new InvalidMasterKeyException("Acceso denegado: La Master Key es incorrecta");
        }

        if (!room.getParticipants().contains(request.getUserName())) {
            room.getParticipants().add(request.getUserName());
        }

        roomRepository.save(room);
        return new AdminAccessResponse(room.getId(), "ADMIN");
    }

    public void removeParticipant(RemoveParticipantRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
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
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));

        if (!room.getParticipants().contains(request.getUsername()) && !room.getAdminName().equals(request.getUsername())) {
            throw new RuntimeException("El usuario no pertenece a la sala");
        }

        Note newNote = new Note(request.getNoteTitle(), request.getContent(), request.getUsername());

        if (room.getCalendar() == null) {
            room.setCalendar(new Calendar());
        }

        room.getCalendar().addNote(newNote);
        roomRepository.save(room);
    }

    public void updateNote(NoteRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
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
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));

        Note note = room.getCalendar().getNotes().stream()
                .filter(n -> n.getTitle().equals(request.getNoteTitle()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nota no encontrada"));

        if (!note.getCreatedBy().equals(request.getUsername())) {
            throw new RuntimeException("No tienes permisos para eliminar esta nota");
        }

        note.delete();
        roomRepository.save(room);
    }

    // ==========================================
    // SECCIÓN DE GESTIÓN DE TAREAS (TASKS)
    // ==========================================

    public void createTask(TaskRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
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

        if (room.getCalendar() == null) {
            room.setCalendar(new Calendar());
        }

        room.getCalendar().addTask(newTask);
        roomRepository.save(room);
    }

    public void updateTask(TaskRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));

        Task task = room.getCalendar().getTasks().stream()
                .filter(t -> t.getTitle().equals(request.getTaskTitle()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        task.edit(request.getTaskTitle(), request.getDescription(), request.getDueDate());
        roomRepository.save(room);
    }

    public void completeTask(TaskRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Sala no encontrada"));

        Task task = room.getCalendar().getTasks().stream()
                .filter(t -> t.getTitle().equals(request.getTaskTitle()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        task.complete();
        roomRepository.save(room);
    }
}
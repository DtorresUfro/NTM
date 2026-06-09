package com.Ntm.service;

import com.Ntm.dto.*;
import com.Ntm.entity.Room;
import com.Ntm.exception.*;
import com.Ntm.repository.RoomRepository;
import org.springframework.stereotype.Service;

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
}
package com.Ntm.service;

import com.Ntm.dto.CreateRoomRequest;
import com.Ntm.dto.CreateRoomResponse;
import com.Ntm.entity.Room;
import com.Ntm.exception.InvalidRoomException;
import com.Ntm.repository.RoomRepository;
import org.springframework.stereotype.Service;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
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
}
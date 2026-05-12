package com.Ntm;

import java.util.ArrayList;
import java.util.List;

public class RoomManager {

    //temporal para probar los metodos
    private List<Room> rooms = new ArrayList<>();


    public Room createRoom(String roomName, String adminName) {
        if (roomName == null || roomName.isEmpty() || adminName == null || adminName.isEmpty()) {
            throw new IllegalArgumentException("Error: Datos insuficientes para crear la sala.");
        }

        Room newRoom = new Room(roomName, adminName);

        rooms.add(newRoom);
        return newRoom;
    }

    public Room joinRoom(String joinId, String userName) {
        if (joinId == null || userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("Error: ID o nombre de usuario no proporcionados.");
        }

        for (Room room : rooms) {
            if (room.getId().equals(joinId)) {
                room.addParticipant(userName);
                return room;
            }
        }

        throw new RuntimeException("Acceso denegado: El ID de la sala no existe.");
    }

    public List<Room> getRooms() {
        return rooms;
    }
}
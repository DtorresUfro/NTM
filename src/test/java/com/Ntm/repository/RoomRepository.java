package com.Ntm.repository;

import com.Ntm.entity.Room;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Test
    void shouldFindRoomByRoomId() {
        Room room = new Room("Sala Test", "Ivan");
        roomRepository.save(room);

        Optional<Room> found = roomRepository.findByRoomId(room.getId());

        assertTrue(found.isPresent());
        assertEquals(room.getId(), found.get().getId());
        assertEquals("Sala Test", found.get().getName());
        assertEquals("Ivan", found.get().getAdminName());
    }

    @Test
    void shouldReturnEmptyWhenRoomIdDoesNotExist() {
        Optional<Room> found = roomRepository.findByRoomId("NO-EXISTE");

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindRoomByMasterKey() {
        Room room = new Room("Sala Master", "Ivan");
        roomRepository.save(room);

        Optional<Room> found = roomRepository.findByMasterKey(room.getMasterKey());

        assertTrue(found.isPresent());
        assertEquals(room.getMasterKey(), found.get().getMasterKey());
    }

    @Test
    void shouldReturnEmptyWhenMasterKeyDoesNotExist() {
        Optional<Room> found = roomRepository.findByMasterKey("MK-INVALIDA");

        assertTrue(found.isEmpty());
    }

    // Persistencia de colecciones

    @Test
    void shouldPersistParticipantsAfterReload() {
        Room room = new Room("Sala Participantes", "Ivan");
        room.addParticipant("Dyssio");
        room.addParticipant("Barbara");
        roomRepository.save(room);

        roomRepository.flush();

        Room reloaded = roomRepository.findByRoomId(room.getId()).orElseThrow();

        assertEquals(3, reloaded.getParticipants().size());
        assertTrue(reloaded.getParticipants().containsAll(List.of("Ivan", "Dyssio", "Barbara")));
    }

    @Test
    void shouldPersistDisconnectedParticipantsAfterReload() {
        Room room = new Room("Sala Desconectados", "Dyssio");
        room.getDisconnectedParticipants().add("Ivan");
        roomRepository.save(room);

        roomRepository.flush();

        Room reloaded = roomRepository.findByRoomId(room.getId()).orElseThrow();

        assertEquals(1, reloaded.getDisconnectedParticipants().size());
        assertEquals("Ivan", reloaded.getDisconnectedParticipants().get(0));
    }

    // Eliminación

    @Test
    void shouldDeleteRoomByMasterKey() {
        Room room = new Room("Sala a Eliminar", "Valen");
        roomRepository.save(room);

        roomRepository.deleteById(room.getMasterKey());

        assertTrue(roomRepository.findByRoomId(room.getId()).isEmpty());
        assertEquals(0, roomRepository.count());
    }

    @Test
    void shouldPersistMultipleRoomsIndependently() {
        Room room1 = new Room("Sala 1", "Ivan");
        Room room2 = new Room("Sala 2", "Barbara");

        roomRepository.save(room1);
        roomRepository.save(room2);

        assertEquals(2, roomRepository.count());
        assertNotEquals(
                roomRepository.findByRoomId(room1.getId()).orElseThrow().getMasterKey(),
                roomRepository.findByRoomId(room2.getId()).orElseThrow().getMasterKey()
        );
    }
}
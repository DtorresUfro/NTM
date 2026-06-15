package com.Ntm.TestUnitarios.repository;

import com.Ntm.TestUnitarios.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, String> {

    @Query("SELECT r FROM Room r WHERE r.id = :roomId")
    Optional<Room> findByRoomId(@Param("roomId") String roomId);

    Optional<Room> findByMasterKey(String masterKey);
}

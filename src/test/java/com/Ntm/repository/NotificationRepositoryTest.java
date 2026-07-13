package com.ntm.repository;

import com.ntm.entity.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void shouldNotMixNotificationsFromDifferentRooms() {
        notificationRepository.save(new Notification("MK-AAAA", "Ivan", "Aviso sala A"));
        notificationRepository.save(new Notification("MK-BBBB", "Ivan", "Aviso sala B"));

        List<Notification> resultRoomA =
                notificationRepository.findByRoomMasterKeyAndTargetUser("MK-AAAA", "Ivan");

        assertEquals(1, resultRoomA.size());
        assertEquals("Aviso sala A", resultRoomA.get(0).getMessage());
    }

    @Test
    void shouldNotMixNotificationsFromDifferentUsers() {
        notificationRepository.save(new Notification("MK-1234", "Ivan", "Para Ivan"));
        notificationRepository.save(new Notification("MK-1234", "Barbara", "Para Barbara"));

        List<Notification> resultIvan =
                notificationRepository.findByRoomMasterKeyAndTargetUser("MK-1234", "Ivan");

        assertEquals(1, resultIvan.size());
        assertEquals("Para Ivan", resultIvan.get(0).getMessage());
    }
}
package com.Ntm.repository;

import com.Ntm.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRoomMasterKeyAndTargetUser(String roomMasterKey, String targetUser);
}

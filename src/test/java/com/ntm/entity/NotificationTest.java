package com.ntm.entity;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void shouldUseAllSettersAndGetters() {

        Notification notification = new Notification();

        notification.setRoomMasterKey("ROOM-123");
        notification.setTargetUser("Ivan");
        notification.setMessage("Notificacion Prueba");
        notification.setReadStatus(true);

        assertEquals("ROOM-123", notification.getRoomMasterKey());
        assertEquals("Ivan", notification.getTargetUser());
        assertEquals("Notificacion Prueba", notification.getMessage());
        assertTrue(notification.isReadStatus());

        assertNotNull(notification.getCreatedAt());
        assertTrue(notification.getCreatedAt() instanceof Date);
    }

    @Test
    void shouldMarkNotificationAsRead() throws Exception {
        Notification notification = new Notification(
                "ROOM-123", "Valen", "Nueva tarea creada");

        notification.markAsRead();

        Field readField = Notification.class.getDeclaredField("readStatus");

        readField.setAccessible(true);

        assertTrue((Boolean) readField.get(notification));
    }

    @Test
    void shouldInitializeCreationDate() throws Exception {
        Notification notification = new Notification(
                "ROOM-123", "Valen", "Mensaje");

        Field createdAtField = Notification.class.getDeclaredField("createdAt");

        createdAtField.setAccessible(true);

        Object value = createdAtField.get(notification);

        assertNotNull(value);
        assertInstanceOf(Date.class, value);
    }
}
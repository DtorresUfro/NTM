package com.Ntm;

import com.Ntm.entity.Notification;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {
    /*
    Estos test son temporales con fines de cobertura,
    hasta que se implementen las notificaciones correctamente.
     */

    @Test
    void shouldCreateNotification() throws Exception {
        Notification notification = new Notification(
                "ROOM-123", "Valen", "Nueva tarea creada");

        Field messageField = Notification.class.getDeclaredField("message");

        messageField.setAccessible(true);

        assertEquals("Nueva tarea creada", messageField.get(notification));
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

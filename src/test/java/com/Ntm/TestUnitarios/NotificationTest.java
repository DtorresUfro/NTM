package com.Ntm.TestUnitarios;

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
        Date date = new Date();

        Notification notification = new Notification(
                "Nueva tarea creada", false, date);

        Field messageField = Notification.class.getDeclaredField("message");

        messageField.setAccessible(true);

        assertEquals("Nueva tarea creada", messageField.get(notification));
    }

    @Test
    void shouldMarkNotificationAsRead() throws Exception {
        Notification notification = new Notification(
                "Nueva tarea creada", false, new Date());

        notification.markAsRead();

        Field readField = Notification.class.getDeclaredField("read");

        readField.setAccessible(true);

        assertTrue((Boolean) readField.get(notification));
    }

    @Test
    void shouldInitializeCreationDate() throws Exception {
        Notification notification = new Notification(
                "Mensaje", false, new Date());

        Field createdAtField = Notification.class.getDeclaredField("createdAd");

        createdAtField.setAccessible(true);

        Object value = createdAtField.get(notification);

        assertNotNull(value);
        assertInstanceOf(Date.class, value);
    }
}
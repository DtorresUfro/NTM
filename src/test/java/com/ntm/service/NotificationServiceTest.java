package com.ntm.service;

import com.ntm.entity.Notification;
import com.ntm.entity.Task;
import com.ntm.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository repository;

    @InjectMocks
    private NotificationService service;

    @Test
    void shouldCreateNotificationWhenTaskIsOverdue() {
        Task task = new Task();
        task.setTitle("Tarea");

        task.setDueDate(new Date(System.currentTimeMillis() - 100000));
        service.createTaskNotification(task, "ROOM1", "Ivan");

        verify(repository).save(any(Notification.class));
    }

    @Test
    void shouldNotCreateNotificationWhenTaskIsNotOverdue() {
        Task task = new Task();
        task.setTitle("Tarea");

        task.setDueDate(new Date(System.currentTimeMillis() + 100000));
        service.createTaskNotification(task, "ROOM1", "Ivan");

        verify(repository, never()).save(any());
    }

    @Test
    void shouldReturnNotifications() {
        List<Notification> notifications = List.of(
                new Notification("ROOM","Ivan","Hola")
        );

        when(repository.findByRoomMasterKeyAndTargetUser(
                "ROOM","Ivan"))
                .thenReturn(notifications);
        List<Notification> result =
                service.getNotifications("ROOM","Ivan");

        assertEquals(1,result.size());
    }

    @Test
    void shouldMarkNotificationAsRead() {
        Notification notification =
                new Notification("ROOM","Ivan","Mensaje");
        when(repository.findById(1L))
                .thenReturn(Optional.of(notification));
        service.read(1L);
        assertTrue(notification.isReadStatus());

        verify(repository).save(notification);

    }

    @Test
    void shouldThrowExceptionWhenNotificationDoesNotExist(){
        when(repository.findById(10L))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                ()-> service.read(10L));
    }


}
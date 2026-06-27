package com.Ntm.service;


import com.Ntm.entity.Notification;
import com.Ntm.entity.Task;
import com.Ntm.repository.NotificationRepository;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
public class NotificationService {


    private final NotificationRepository repository;


    public NotificationService(
            NotificationRepository repository
    ) {

        this.repository = repository;

    }


    public void createTaskNotification(
            Task task,
            String room,
            String user
    ) {


        if (task.isOverdue()) {


            Notification notification =
                    new Notification(
                            room,
                            user,
                            "La tarea '"
                                    + task.getTitle()
                                    + "' está atrasada"
                    );


            repository.save(notification);

        }


    }


    public List<Notification> getNotifications(
            String room,
            String user
    ) {

        return repository
                .findByRoomMasterKeyAndTargetUser(
                        room,
                        user
                );

    }


    public void read(Long id) {

        Notification n =
                repository.findById(id)
                        .orElseThrow();


        n.markAsRead();

        repository.save(n);

    }

}
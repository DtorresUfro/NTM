package com.Ntm.controller;


import com.Ntm.entity.Notification;
import com.Ntm.service.NotificationService;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/notifications")
@CrossOrigin
public class NotificationController {


    private final NotificationService service;


    public NotificationController(
            NotificationService service
    ) {

        this.service = service;

    }


    @GetMapping("/{room}/{user}")
    public List<Notification> get(
            @PathVariable String room,
            @PathVariable String user
    ) {

        return service.getNotifications(
                room,
                user
        );

    }


    @PutMapping("/{id}/read")
    public void read(
            @PathVariable Long id
    ) {

        service.read(id);

    }

}
package com.ntm.controller;


import com.ntm.entity.Notification;
import com.ntm.service.NotificationService;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
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
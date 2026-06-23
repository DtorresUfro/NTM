package com.Ntm.controller;

import com.Ntm.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@Controller
public class ViewController {

    @Autowired
    private RoomService roomService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/crear-sala")
    public String crearSala() {
        return "crear-sala";
    }

    @GetMapping("/sala-creada")
    public String salaCreada() {
        return "sala-creada";
    }

    @GetMapping("/join-options")
    public String joinOptions() {
        return "join-options";
    }

    @GetMapping("/unirse-sala")
    public String unirseSalaForm() {
        return "unirse-sala";
    }

    @GetMapping("/admin-access")
    public String adminAccess() {
        return "admin-access";
    }

    @GetMapping("/room/{roomId}")
    public String room(@PathVariable String roomId, Model model) {
        model.addAttribute("roomId", roomId);

        try {
            List<String> participants = roomService.getRoomParticipants(roomId);
            model.addAttribute("participants", participants);
        } catch (Exception e) {
            model.addAttribute("participants", List.of());
        }

        return "room";
    }
}
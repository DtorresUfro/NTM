package com.Ntm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/crear-sala")
    public String crearSala() {
        return "crear-sala";
    }

    @GetMapping("/unirse-a-sala")
    public String unirseASala() {
        return "unirse-a-sala";
    }

    @GetMapping("/sala-creada")
    public String salaCreada() {
        return "sala-creada";
    }

    @GetMapping("/room/{roomId}")
    public String room(@PathVariable String roomId, Model model) {
        model.addAttribute("roomId", roomId);
        return "room";
    }

    @GetMapping("/admin-access")
    public String adminAccess() {
        return "admin-access";
    }

    @GetMapping("/room/{roomId}/admin")
    public String adminPanel(@PathVariable String roomId, Model model) {
        model.addAttribute("roomId", roomId);
        return "admin";
    }
}
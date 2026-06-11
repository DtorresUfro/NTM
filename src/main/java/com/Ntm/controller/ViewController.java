package com.Ntm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {


     // Página principal.
     //Crear sala y unirse a sala.

    @GetMapping("/")
    public String home() {
        return "index";
    }


     //Vista principal de una sala.
     //Participantes, tareas, notas y notificaciones.

    @GetMapping("/room/{roomId}")
    public String room(@PathVariable String roomId, Model model) {
        model.addAttribute("roomId", roomId);
        return "room";
    }

    // Acceso mediante Master Key.

    @GetMapping("/admin-access")
    public String adminAccess() {
        return "admin-access";
    }

    /*
    Panel administrativo.
    funciones exclusivas del administrador.
     */
    @GetMapping("/room/{roomId}/admin")
    public String adminPanel(@PathVariable String roomId, Model model) {
        model.addAttribute("roomId", roomId);
        return "admin";
    }
}
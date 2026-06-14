package com.Ntm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    // 1. Página principal (Crear sala o ir a opciones de unión)
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
    // ==========================================

    // 2. NUEVA VISTA INTERMEDIA: Selección del tipo de acceso
    @GetMapping("/join-options")
    public String joinOptions() {
        return "join-options";
    }

    // 3. Formulario para participantes normales (Unirse mediante ID)
    @GetMapping("/unirse-sala")
    public String unirseSalaForm() {
        return "unirse-sala";
    }

    // 4. Formulario para el Administrador (Acceso mediante Master Key)
    @GetMapping("/admin-access")
    public String adminAccess() {
        return "admin-access";
    }

    // 5. Vista principal de una sala (Participantes, tareas, notas)
    @GetMapping("/room/{roomId}")
    public String room(@PathVariable String roomId, Model model) {
        model.addAttribute("roomId", roomId);
        return "room";
    }

    // 6. Panel administrativo exclusivo
    @GetMapping("/room/{roomId}/admin")
    public String adminPanel(@PathVariable String roomId, Model model) {
        model.addAttribute("roomId", roomId);
        return "admin";
    }
}
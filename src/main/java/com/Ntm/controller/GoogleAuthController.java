package com.Ntm.controller;

import com.Ntm.service.GoogleCalendarAuthService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GoogleAuthController {

    private final GoogleCalendarAuthService authService;

    public GoogleAuthController(GoogleCalendarAuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/auth/google")
    public String authenticate() {
        return "redirect:" + authService.getAuthorizationUrl();
    }

    @GetMapping("/callback")
    @ResponseBody
    public String callback(@RequestParam("code") String code) {
        try {
            authService.exchangeCodeForCredential(code);
            return "Autenticación con Google Calendar completada correctamente.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error durante la autenticación: " + e.getMessage();
        }
    }
}
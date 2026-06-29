package com.ntm.controller;

import com.ntm.service.GoogleCalendarAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GoogleAuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleAuthController.class);

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
            LOGGER.warn("Google Calendar authentication failed: {}", e.getMessage());
            return "Error durante la autenticacion con Google Calendar.";
        }
    }
}
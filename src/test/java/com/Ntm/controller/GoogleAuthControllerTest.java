package com.Ntm.controller;

import com.Ntm.service.GoogleCalendarAuthService;
import com.google.api.client.auth.oauth2.Credential;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GoogleAuthControllerTest {

    @Mock
    private GoogleCalendarAuthService authService;

    @InjectMocks
    private GoogleAuthController controller;

    private MockMvc mockMvc;

    @Test
    void shouldRedirectToGoogleAuth() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        when(authService.getAuthorizationUrl()).thenReturn("https://accounts.google.com/auth");

        mockMvc.perform(get("/auth/google"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("https://accounts.google.com/auth"));
    }

    @Test
    void shouldHandleCallbackSuccessfully() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        Credential mockCredential = mock(Credential.class);
        when(authService.exchangeCodeForCredential(anyString())).thenReturn(mockCredential);

        mockMvc.perform(get("/callback").param("code", "auth-code-123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Autenticación con Google Calendar completada correctamente."));
    }
}
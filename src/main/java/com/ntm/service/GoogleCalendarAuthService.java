package com.ntm.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.ntm.exception.GoogleCalendarException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleCalendarAuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleCalendarAuthService.class);

    private static final String APPLICATION_NAME = "NoteTask";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

    @Value("${google.client.client-id}")
    private String clientId;

    @Value("${google.client.client-secret}")
    private String clientSecret;

    @Value("${google.client.redirect-uri}")
    private String redirectUri;

    private GoogleAuthorizationCodeFlow flow;
    private com.google.api.client.http.HttpTransport httpTransport;

    @PostConstruct
    public void init() {
        try {
            if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
                LOGGER.warn("GOOGLE_CLIENT_ID o GOOGLE_CLIENT_SECRET no configurados. La integracion con Google Calendar estara deshabilitada.");
                return;
            }
            this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH));

            GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
            clientSecrets.setInstalled(new GoogleClientSecrets.Details()
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setRedirectUris(Collections.singletonList(redirectUri)));

            this.flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(dataStoreFactory)
                    .setAccessType("offline")
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            LOGGER.warn("Error al inicializar Google Calendar: {}. La integracion estara deshabilitada.", e.getMessage());
        }
    }

    /**
     * Genera la URL de autorizacion para redirigir al usuario a Google.
     */
    public String getAuthorizationUrl() {
        if (flow == null) {
            throw new GoogleCalendarException("Google Calendar no esta configurado.");
        }
        return flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .setAccessType("offline")
                .build();
    }

    /**
     * Intercambia el codigo de autorizacion por un token de acceso.
     */
    public Credential exchangeCodeForCredential(String code) {
        if (flow == null) {
            throw new GoogleCalendarException("Google Calendar no esta configurado.");
        }
        try {
            TokenResponse tokenResponse = flow.newTokenRequest(code)
                    .setRedirectUri(redirectUri)
                    .execute();

            return flow.createAndStoreCredential(tokenResponse, "user");
        } catch (IOException e) {
            throw new GoogleCalendarException("No se pudo obtener credenciales de Google Calendar.", e);
        }
    }

    /**
     * Obtiene las credenciales guardadas si el usuario ya autorizo.
     */
    public Credential getStoredCredentials() {
        if (flow == null) {
            return null;
        }
        try {
            return flow.loadCredential("user");
        } catch (IOException e) {
            throw new GoogleCalendarException("No se pudo leer las credenciales de Google Calendar.", e);
        }
    }

    /**
     * Obtiene el servicio de Calendar con las credenciales guardadas.
     */
    public Calendar getCalendarService() {
        Credential credential = getStoredCredentials();
        if (credential == null) {
            return null;
        }

        try {
            if (credential.getExpiresInSeconds() != null && credential.getExpiresInSeconds() <= 60) {
                credential.refreshToken();
            }
        } catch (IOException e) {
            throw new GoogleCalendarException("No se pudo refrescar el token de Google Calendar.", e);
        }

        return new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Verifica si el usuario ya esta autenticado.
     */
    public boolean isAuthenticated() {
        return getStoredCredentials() != null;
    }
}
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
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
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
    private FileDataStoreFactory dataStoreFactory;
    private com.google.api.client.http.HttpTransport httpTransport;

    @PostConstruct
    public void init() {
        try {
            if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
                LOGGER.warn("GOOGLE_CLIENT_ID o GOOGLE_CLIENT_SECRET no configurados. La integración con Google Calendar estará deshabilitada.");
                return;
            }
            this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            this.dataStoreFactory = new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH));

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
        } catch (Exception e) {
            LOGGER.warn("Error al inicializar Google Calendar: {}. La integración estará deshabilitada.", e.getMessage());
        }
    }

    /**
     * Genera la URL de autorización para redirigir al usuario a Google
     */
    public String getAuthorizationUrl() {
        if (flow == null) throw new RuntimeException("Google Calendar no está configurado.");
        return flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .setAccessType("offline")
                .build();
    }

    /**
     * Intercambia el código de autorización por un token de acceso (Credential)
     */
    public Credential exchangeCodeForCredential(String code) throws Exception {
        if (flow == null) throw new RuntimeException("Google Calendar no está configurado.");
        TokenResponse tokenResponse = flow.newTokenRequest(code)
                .setRedirectUri(redirectUri)
                .execute();

        return flow.createAndStoreCredential(tokenResponse, "user");
    }

    /**
     * Obtiene las credenciales guardadas (si el usuario ya autorizó)
     */
    public Credential getStoredCredentials() throws Exception {
        if (flow == null) return null;
        return flow.loadCredential("user");
    }

    /**
     * Obtiene el servicio de Calendar (con las credenciales guardadas)
     * Si no hay credenciales guardadas, devuelve null
     */
    public Calendar getCalendarService() throws Exception {
        Credential credential = getStoredCredentials();
        if (credential == null) {
            return null;
        }

        // Verificar si el token expiró y refrescarlo
        if (credential.getExpiresInSeconds() != null && credential.getExpiresInSeconds() <= 60) {
            credential.refreshToken();
        }

        return new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Verifica si el usuario ya está autenticado
     */
    public boolean isAuthenticated() throws Exception {
        return getStoredCredentials() != null;
    }
}
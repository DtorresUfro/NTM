package com.Ntm.service;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

class GoogleCalendarAuthServiceTest {

    @Test
    void shouldReturnFalseWhenNotAuthenticated() throws Exception {

        GoogleCalendarAuthService service = new GoogleCalendarAuthService();
        assertFalse(service.isAuthenticated());
    }

    @Test
    void shouldGenerateAuthorizationUrlWhenConfigured() {
        GoogleCalendarAuthService service = new GoogleCalendarAuthService();

        setField(service, "clientId", "test-client-id");
        setField(service, "clientSecret", "test-client-secret");
        setField(service, "redirectUri", "http://localhost:8080/callback");

        service.init();
        String url = service.getAuthorizationUrl();

        assertNotNull(url);
        assertTrue(url.contains("accounts.google.com"));
        assertTrue(url.contains("client_id=test-client-id"));
        assertTrue(url.contains("redirect_uri=http://localhost:8080/callback"));
    }

    @Test
    void shouldNotFailWhenRedirectUriIsMissing() {
        GoogleCalendarAuthService service = new GoogleCalendarAuthService();

        setField(service, "clientId", "test-client-id");
        setField(service, "clientSecret", "test-client-secret");
        setField(service, "redirectUri", "");

        assertDoesNotThrow(service::init);
    }

    @Test
    void shouldReturnNullStoredCredentialsWhenFlowIsNull() throws Exception {

        GoogleCalendarAuthService service = new GoogleCalendarAuthService();
        assertNull(service.getStoredCredentials());
    }

    @Test
    void shouldThrowExceptionWhenGoogleIsNotConfigured() {
        GoogleCalendarAuthService service = new GoogleCalendarAuthService();
        assertThrows(RuntimeException.class,
                service::getAuthorizationUrl);
    }

    @Test
    void shouldThrowExceptionWhenExchangingWithoutConfiguration() {

        GoogleCalendarAuthService service = new GoogleCalendarAuthService();
        assertThrows(RuntimeException.class,
                () -> service.exchangeCodeForCredential("codigo"));
    }

    @Test
    void shouldReturnNullCalendarServiceWhenNoCredentials() throws Exception {

        GoogleCalendarAuthService service = new GoogleCalendarAuthService();
        assertNull(service.getCalendarService());
    }

    @Test
    void shouldNotInitializeFlowWhenClientConfigurationIsMissing() {

        GoogleCalendarAuthService service = new GoogleCalendarAuthService();

        setField(service, "clientId", "");
        setField(service, "clientSecret", "");
        setField(service, "redirectUri", "");

        assertDoesNotThrow(service::init);
    }

    @Test
    void shouldNotFailWhenCredentialsArePartiallyConfigured() {
        GoogleCalendarAuthService service = new GoogleCalendarAuthService();

        setField(service, "clientId", "test-client-id");
        setField(service, "clientSecret", "");
        setField(service, "redirectUri", "http://localhost:8080/callback");

        assertDoesNotThrow(service::init);
    }

    /**
     * Método auxiliar para modificar atributos privados mediante Reflection.
     */
    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
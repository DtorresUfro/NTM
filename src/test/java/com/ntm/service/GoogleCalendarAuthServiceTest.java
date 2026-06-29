package com.ntm.service;

import com.ntm.exception.GoogleCalendarException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class GoogleCalendarAuthServiceTest {

    @Test
    void shouldReturnFalseWhenNotAuthenticated() {
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

    @ParameterizedTest
    @CsvSource({
            "test-client-id,test-client-secret,''",
            "'', '', ''",
            "test-client-id,'',http://localhost:8080/callback"
    })
    void shouldNotFailWhenClientConfigurationIsIncomplete(
            String clientId,
            String clientSecret,
            String redirectUri
    ) {
        GoogleCalendarAuthService service = new GoogleCalendarAuthService();

        setField(service, "clientId", clientId);
        setField(service, "clientSecret", clientSecret);
        setField(service, "redirectUri", redirectUri);

        assertDoesNotThrow(service::init);
    }

    @Test
    void shouldReturnNullStoredCredentialsWhenFlowIsNull() {
        GoogleCalendarAuthService service = new GoogleCalendarAuthService();

        assertNull(service.getStoredCredentials());
    }

    @Test
    void shouldThrowExceptionWhenGoogleIsNotConfigured() {
        GoogleCalendarAuthService service = new GoogleCalendarAuthService();

        assertThrows(GoogleCalendarException.class,
                service::getAuthorizationUrl);
    }

    @Test
    void shouldThrowExceptionWhenExchangingWithoutConfiguration() {
        GoogleCalendarAuthService service = new GoogleCalendarAuthService();

        assertThrows(GoogleCalendarException.class,
                () -> service.exchangeCodeForCredential("codigo"));
    }

    @Test
    void shouldReturnNullCalendarServiceWhenNoCredentials() {
        GoogleCalendarAuthService service = new GoogleCalendarAuthService();

        assertNull(service.getCalendarService());
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }
}
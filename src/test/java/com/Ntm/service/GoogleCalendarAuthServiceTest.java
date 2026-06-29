package com.Ntm.service;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

class GoogleCalendarAuthServiceTest {

    //Verifica que el usuario no esté autenticado cuando no existen credenciales.
    @Test
    void shouldReturnFalseWhenNotAuthenticated() throws Exception {

        GoogleCalendarAuthService service = new GoogleCalendarAuthService();
        assertFalse(service.isAuthenticated());
    }

    //Verifica que no existan credenciales almacenadas cuando la autenticación aún no inició.
    @Test
    void shouldReturnNullStoredCredentialsWhenFlowIsNull() throws Exception {

        GoogleCalendarAuthService service = new GoogleCalendarAuthService();
        assertNull(service.getStoredCredentials());
    }

    //Excepción al obtener la URL de autorización sin configurar Google Calendar
    @Test
    void shouldThrowExceptionWhenGoogleIsNotConfigured() {

        GoogleCalendarAuthService service = new GoogleCalendarAuthService();
        assertThrows(RuntimeException.class,
                service::getAuthorizationUrl);
    }

    //Excepción al intercambiar un código de autorización sin haber configurado Google Calendar.
    @Test
    void shouldThrowExceptionWhenExchangingWithoutConfiguration() {

        GoogleCalendarAuthService service = new GoogleCalendarAuthService();
        assertThrows(RuntimeException.class,
                () -> service.exchangeCodeForCredential("codigo"));
    }

    //El servicio de Google Calendar es nulo cuando no existen credenciales.
    @Test
    void shouldReturnNullCalendarServiceWhenNoCredentials() throws Exception {

        GoogleCalendarAuthService service = new GoogleCalendarAuthService();
        assertNull(service.getCalendarService());
    }

    //La inicialización no falla cuando las credenciales de Google no están configuradas.
    @Test
    void shouldNotInitializeFlowWhenClientConfigurationIsMissing() {

        GoogleCalendarAuthService service = new GoogleCalendarAuthService();

        setField(service, "clientId", "");
        setField(service, "clientSecret", "");
        setField(service, "redirectUri", "");

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
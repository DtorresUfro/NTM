package com.Ntm.service;

import com.Ntm.dto.CreateRoomRequest;
import com.Ntm.dto.CreateRoomResponse;
import com.Ntm.entity.Room;
import com.Ntm.exception.InvalidRoomException;
import com.Ntm.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomServiceTest {

    private RoomRepository roomRepository;
    private RoomService roomService;

    /**
     * Inicializa el entorno de prueba antes de la ejecución de cada método individual.
     * Crea un simulacro (Mock) de la capa de datos e inyecta la dependencia en el servicio.
     */
    @BeforeEach
    void setUp() {
        this.roomRepository = mock(RoomRepository.class);
        this.roomService = new RoomService(roomRepository);
    }

    /**
     * Verifica que una solicitud con datos válidos cree y persista la sala correctamente.
     */
    @Test
    void shouldCreateRoomSuccessfully() {
        // Arrange: Preparación del escenario de prueba y datos de entrada
        CreateRoomRequest request = new CreateRoomRequest();
        setField(request, "roomName", "Sala Test");
        setField(request, "adminName", "Valen");

        // Act: Ejecución del método bajo análisis
        CreateRoomResponse response = roomService.createRoom(request);

        // Assert: Verificación de la estructura y datos del DTO de salida
        assertNotNull(response);
        assertEquals("Sala Test", response.getRoomName());
        assertNotNull(response.getRoomId());
        assertNotNull(response.getMasterKey());

        // Assert: Verificación del comportamiento de persistencia en la base de datos
        ArgumentCaptor<Room> captor = ArgumentCaptor.forClass(Room.class);
        verify(roomRepository).save(captor.capture());

        Room savedRoom = captor.getValue();
        assertEquals("Sala Test", savedRoom.getName());
        assertEquals("Valen", savedRoom.getAdminName());
        assertTrue(savedRoom.getParticipants().contains("Valen"));
    }

    /**
     * Valida que el sistema interrumpa la ejecución si el nombre de la sala es una cadena vacía.
     */
    @Test
    void shouldThrowExceptionWhenRoomNameIsEmpty() {
        // Arrange: Configuración de datos de entrada inválidos
        CreateRoomRequest request = new CreateRoomRequest();
        setField(request, "roomName", "");
        setField(request, "adminName", "Valen");

        // Act & Assert: Captura de la excepción esperada y validación del mensaje
        InvalidRoomException exception = assertThrows(
                InvalidRoomException.class,
                () -> roomService.createRoom(request)
        );

        assertEquals("El nombre de la sala es obligatorio", exception.getMessage());
    }

    /**
     * Valida que el sistema interrumpa la ejecución si el nombre del administrador está vacío.
     */
    @Test
    void shouldThrowExceptionWhenAdminNameIsEmpty() {
        // Arrange: Configuración de datos de entrada inválidos
        CreateRoomRequest request = new CreateRoomRequest();
        setField(request, "roomName", "Sala Test");
        setField(request, "adminName", "");

        // Act & Assert: Captura de la excepción esperada y validación del mensaje
        InvalidRoomException exception = assertThrows(
                InvalidRoomException.class,
                () -> roomService.createRoom(request)
        );

        assertEquals("El nombre del administrador es obligatorio", exception.getMessage());
    }

    /**
     * Permite modificar atributos privados en objetos inmutables que carecen de métodos modificadores (Setters).
     *
     * @param target    Objeto destino sobre el cual se aplicará el cambio.
     * @param fieldName Nombre técnico de la variable privada a modificar.
     * @param value     Valor que se inyectará directamente en memoria.
     */
    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Fallo en Reflection al intentar escribir el campo: " + fieldName, e);
        }
    }
}
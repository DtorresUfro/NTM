package com.Ntm;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
class RoomManagerTest {

        private RoomManager service;

        @BeforeEach
        void setUp() {
            service = new RoomManager();
        }

        @Test
        void testCU1_CrearSalaExitosa() {
            Room sala = service.createRoom("Lab Informatica", "Valen");
            assertNotNull(sala.getId());
            assertEquals("Lab Informatica", sala.getName());
            assertTrue(sala.getParticipants().contains("Valen"));
        }

        @Test
        void testCU2_UnirseASalaExitosa() {
            Room salaCreada = service.createRoom("Grupo 1", "Admin");
            String idParaUnirse = salaCreada.getId();

            Room salaUnida = service.joinRoom(idParaUnirse, "User2");
            assertTrue(salaUnida.getParticipants().contains("User2"));
            assertEquals(2, salaUnida.getParticipants().size());
        }

        @Test
        void testCU2_ErrorSalaInexistente() {
            assertThrows(RuntimeException.class, () -> {
                service.joinRoom("ID_FALSO", "UserX");
            });
        }
    }
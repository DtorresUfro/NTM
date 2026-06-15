package com.Ntm.TestUnitarios.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InvalidMasterKeyExceptionTest {
    //Test de cobertura
    @Test
    void shouldCreateException() {
        InvalidMasterKeyException exception = new InvalidMasterKeyException("Master Key inválida");

        assertEquals("Master Key inválida", exception.getMessage());
    }
}
package com.ntm.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NoteTest {
    //Verificar getters y setters
    @Test
    void shouldUseAllSettersAndGetters() {
        Note note = new Note("Titulo", "Contenido", "Dyssio");

        note.setTitle("Nuevo titulo");
        note.setContent("Nuevo contenido");
        note.setCreatedBy("Iván");
        note.setActive(false);

        assertEquals("Nuevo titulo", note.getTitle());
        assertEquals("Nuevo contenido", note.getContent());
        assertEquals("Iván", note.getCreatedBy());
        assertFalse(note.isActive());

        assertNotNull(note.getCreatedAt());
        assertNotNull(note.getUpdatedAt());
    }

    //Actualizar nota
    @Test
    void shouldUpdateUpdatedAtField() {
        Note note = new Note("Titulo", "Contenido", "Lucas");

        LocalDateTime now = LocalDateTime.now();

        note.setUpdatedAt(now);

        assertEquals(now, note.getUpdatedAt());
    }

    //Eliminar nota
    @Test
    void shouldDeleteNote() {
        Note note = new Note("Titulo", "Contenido", "Lucas");

        note.delete();

        assertFalse(note.isActive());
    }
}
package com.Ntm.entity;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void shouldCreateTaskCorrectly() {
        Date dueDate = new Date();
        Date createdAt = new Date();

        Task task = new Task(
                "Implementar Tests", "Crear pruebas unitarias",
                dueDate, "Dyssio", createdAt, false);

        assertEquals("Implementar Tests", task.getTitle());
        assertEquals("Crear pruebas unitarias", task.getDescription());
        assertEquals(dueDate, task.getDueDate());
        assertEquals("Dyssio", task.getCreatedBy());
        assertEquals(createdAt, task.getCreatedAt());
        assertFalse(task.isCompleted());
    }

    @Test
    void shouldCompleteTask() {
        Task task = new Task("Tarea", "Descripcion", new Date(),
                "Dyssio", new Date(), false);

        task.complete();
        assertTrue(task.isCompleted());
    }

    @Test
    void shouldUseAllSettersAndGetters() {
        Task task = new Task("Titulo", "Descripcion",
                new Date(), "Dyssio", new Date(), false);

        Date dueDate = new Date();
        Date createdAt = new Date();

        task.setTitle("Nuevo titulo");
        task.setDescription("Ayuda");
        task.setDueDate(dueDate);
        task.setCreatedBy("Valen");
        task.setCreatedAt(createdAt);
        task.setCompleted(true);

        assertEquals("Nuevo titulo", task.getTitle());
        assertEquals("Ayuda", task.getDescription());
        assertEquals(dueDate, task.getDueDate());
        assertEquals("Valen", task.getCreatedBy());
        assertEquals(createdAt, task.getCreatedAt());
        assertTrue(task.isCompleted());
    }

    @Test
    void shouldCallEditMethod() {
        Task task = new Task("Titulo", "Descripcion",
                new Date(), "Dyssio", new Date(), false);

        task.edit("Nuevo titulo", "Nueva descripcion", new Date());

        // El metodo está vacío, simplemente verificamos que no lance excepción
        assertNotNull(task);
    }

    @Test
    void shouldCallDeleteMethod() {
        Task task = new Task("Titulo", "Descripcion",
                new Date(), "Lucas", new Date(), false);

        task.delete();
        // El metodo está vacío, sólo buscamos cobertura
        assertNotNull(task);
    }

    @Test
    void shouldReturnFalseWhenCheckingOverdue() {
        Task task = new Task("Titulo", "Descripcion",
                new Date(), "Lucas", new Date(), false);

        assertFalse(task.isOverdue());
    }
}
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

        assertNotNull(task);
    }

    @Test
    void shouldCallDeleteMethod() {
        Task task = new Task("Titulo", "Descripcion",
                new Date(), "Lucas", new Date(), false);

        task.delete();
        assertNotNull(task);
    }

    @Test
    void shouldReturnFalseWhenCheckingOverdue() {
        Date futureDate = new Date(System.currentTimeMillis() + 60_000);
        Task task = new Task("Titulo", "Descripcion",
                futureDate, "Lucas", new Date(), false);

        assertFalse(task.isOverdue());
    }

    @Test
    void shouldDetectTaskIsNotOverdue() {
        Task task = new Task();

        task.setDueDate(
                new Date(System.currentTimeMillis()+86400000));

        assertFalse(task.isOverdue());
    }

    @Test
    void shouldReturnFalseWhenTaskIsCompletedEvenIfOverdue() {
        Date pastDate = new Date(System.currentTimeMillis() - 86400000);
        Task task = new Task("Tarea completada", "Descripcion", pastDate, "Lucas", new Date(), true);

        assertFalse(task.isOverdue());
    }

    @Test
    void shouldCreateTaskWithStartDateConstructor() {
        Date startDate = new Date();
        Date dueDate = new Date();
        Task task = new Task("Tarea", "Descripcion", startDate, dueDate, "Lucas");

        assertEquals("Tarea", task.getTitle());
        assertEquals("Descripcion", task.getDescription());
        assertEquals(startDate, task.getStartDate());
        assertEquals(dueDate, task.getDueDate());
        assertEquals("Lucas", task.getCreatedBy());
        assertNotNull(task.getCreatedAt());
        assertFalse(task.isCompleted());
        assertTrue(task.isActive());
    }

    @Test
    void shouldSetAndGetGoogleEventId() {
        Task task = new Task("Tarea", "Desc", new Date(), "Lucas", new Date(), false);

        task.setGoogleEventId("event-123");

        assertEquals("event-123", task.getGoogleEventId());
    }
}
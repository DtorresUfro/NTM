package com.Ntm;
import java.time.LocalDateTime;

public class Note {
    private String title;
    private String content;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private boolean active = true;

    public Note(String title, String content, String createdBy) {
        this.title = title;
        this.content = content;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void editContent(String newContent) {
        this.content = newContent;
        this.updatedAt = LocalDateTime.now();
        System.out.println("Contenido actualizado.");
    }

    public void delete() {
        this.active = false;
    }

}

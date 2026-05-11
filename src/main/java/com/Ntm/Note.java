package com.Ntm;
import java.util.Date;

public class Note {
    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private String createdBy;
    private boolean active = true;

    public Note(String title, String content, String createdBy) {
        this.title = title;
        this.content = content;
        this.createdBy = createdBy;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public void editContent(String newContent) {
        this.content = newContent;
        this.updatedAt = new Date();
        System.out.println("Contenido actualizado.");
    }

    public void delete() {
        this.active = false;
    }

}

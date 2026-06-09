package com.Ntm.service;

import com.Ntm.entity.Note;
import com.Ntm.repository.NoteRepository;
import org.springframework.stereotype.Service;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note guardarNota(String title, String content, String createdBy, String roomMasterKey) {
        Note nota = new Note(title, content, createdBy, roomMasterKey);
        return noteRepository.save(nota);
    }
}
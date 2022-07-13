package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {
    private NoteMapper noteMapper;

    public NoteService(NoteMapper noteMapper){
        this.noteMapper = noteMapper;
    }

    public List<Note> getNotes(int userId){
        return noteMapper.getNotes(userId);
    }

    public void addNote(Note note, int userId){
        Note newNote = new Note();
        newNote.setUserId(userId);
        newNote.setNoteDescription(note.getNoteDescription());
        newNote.setNoteTitle(note.getNoteTitle());

        noteMapper.insertNote(newNote);
    }

    public void updateNote(Note note){
        noteMapper.updateNote(note);
    }

    public void deleteNote(int noteId){
        noteMapper.deleteNote(noteId);
    }
}

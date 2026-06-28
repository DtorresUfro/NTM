package com.Ntm.controller;

import com.Ntm.dto.*;
import com.Ntm.entity.Task;
import com.Ntm.entity.Note;
import com.Ntm.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // ==========================================
    // ENDPOINTS PARA SALAS (ROOMS)
    // ==========================================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateRoomResponse createRoom(@RequestBody CreateRoomRequest request) {
        return roomService.createRoom(request);
    }

    @PostMapping("/join")
    public JoinRoomResponse joinRoom(@RequestBody JoinRoomRequest request) {
        return roomService.joinRoom(request);
    }

    @DeleteMapping("/{roomId}")
    public DeleteRoomResponse deleteRoom(@PathVariable String roomId, @RequestBody DeleteRoomRequest request) {
        return roomService.deleteRoom(roomId, request);
    }

    @PostMapping("/validate-masterkey")
    public ResponseEntity<AdminAccessResponse> accessWithMasterKey(@RequestBody AdminAccessRequest request) {
        AdminAccessResponse response = roomService.validateMasterKey(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/remove-participant")
    public ResponseEntity<String> removeParticipant(@RequestBody RemoveParticipantRequest request) {
        try {
            roomService.removeParticipant(request);
            return ResponseEntity.ok("Participante eliminado exitosamente de la sala.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @GetMapping("/{roomId}/members")
    public ResponseEntity<List<RoomMemberResponse>> getMembers(@PathVariable String roomId) {
        try {
            return ResponseEntity.ok(roomService.getRoomMembers(roomId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/leave")
    public ResponseEntity<String> leaveRoom(@RequestBody RemoveParticipantRequest request) {
        try {
            roomService.leaveRoom(request.getRoomId(), request.getUsernameToRemove());
            return ResponseEntity.ok("Usuario retirado exitosamente de la sala.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/presence")
    public ResponseEntity<String> markPresence(@RequestBody RemoveParticipantRequest request) {
        try {
            roomService.markUserConnected(request.getRoomId(), request.getUsernameToRemove());
            return ResponseEntity.ok("Usuario conectado en la sala.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // ==========================================
    // ENDPOINTS PARA TAREAS (TASKS)
    // ==========================================

    @GetMapping("/{roomId}/tasks")
    public ResponseEntity<List<Task>> getTasks(@PathVariable String roomId) {
        try {
            List<Task> tasks = roomService.getTasksByRoom(roomId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/tasks")
    public ResponseEntity<String> createTask(@RequestBody TaskRequest request) {
        try {
            roomService.createTask(request);
            return ResponseEntity.ok("Tarea agregada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/tasks/complete")
    public ResponseEntity<String> completeTask(@RequestBody TaskRequest request) {
        try {
            roomService.completeTask(request);
            return ResponseEntity.ok("Tarea completada/actualizada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/tasks")
    public ResponseEntity<String> updateTask(@RequestBody TaskRequest request) {
        try {
            roomService.updateTask(request);
            return ResponseEntity.ok("Tarea editada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/tasks")
    public ResponseEntity<String> deleteTask(@RequestBody TaskRequest request) {
        try {
            roomService.deleteTask(request);
            return ResponseEntity.ok("Tarea eliminada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==========================================
    // ENDPOINTS PARA NOTAS (NOTES)
    // ==========================================

    @GetMapping("/{roomId}/notes")
    public ResponseEntity<List<Note>> getNotes(@PathVariable String roomId) {
        try {
            List<Note> notes = roomService.getNotesByRoom(roomId);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/notes")
    public ResponseEntity<String> createNote(@RequestBody NoteRequest request) {
        try {
            roomService.createNote(request);
            return ResponseEntity.ok("Nota agregada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/notes")
    public ResponseEntity<String> updateNote(@RequestBody NoteRequest request) {
        try {
            roomService.updateNote(request);
            return ResponseEntity.ok("Nota editada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/notes")
    public ResponseEntity<String> deleteNote(@RequestBody NoteRequest request) {
        try {
            roomService.deleteNote(request);
            return ResponseEntity.ok("Nota eliminada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

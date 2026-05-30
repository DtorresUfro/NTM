package com.Ntm.controller;

import com.Ntm.dto.*;
import com.Ntm.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

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
        AdminAccessResponse response = roomService.grantAdminAccess(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/remove-participant")
    public ResponseEntity<String> removeParticipant(@RequestBody RemoveParticipantRequest request) {
        try {
            roomService.removeParticipant(request);
            return ResponseEntity.ok("Usuario eliminado de la sala exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/tasks")
    public ResponseEntity<String> createTask(@RequestBody TaskRequest request) {
        try {
            roomService.createTask(request);
            return ResponseEntity.ok("Tarea creada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/tasks")
    public ResponseEntity<String> updateTask(@RequestBody TaskRequest request) {
        try {
            roomService.updateTask(request);
            return ResponseEntity.ok("Tarea actualizada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/tasks/complete")
    public ResponseEntity<String> completeTask(@RequestBody TaskRequest request) {
        try {
            roomService.completeTask(request);
            return ResponseEntity.ok("Tarea completada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
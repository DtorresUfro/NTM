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
}
package com.Ntm.controller;

import com.Ntm.dto.CreateRoomRequest;
import com.Ntm.dto.CreateRoomResponse;
import com.Ntm.dto.JoinRoomRequest;
import com.Ntm.dto.JoinRoomResponse;
import com.Ntm.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
}
package com.example.Chat.App.Backend.with.Authentication.Socket.controller;

import com.example.Chat.App.Backend.with.Authentication.Auth.entity.Users;
import com.example.Chat.App.Backend.with.Authentication.Socket.dto.AddUserRequest;
import com.example.Chat.App.Backend.with.Authentication.Socket.dto.CreateRoomRequest;
import com.example.Chat.App.Backend.with.Authentication.Socket.entity.ChatRoom;
import com.example.Chat.App.Backend.with.Authentication.Socket.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/create")
    public ResponseEntity<ChatRoom> createRoom(@RequestBody CreateRoomRequest request, @AuthenticationPrincipal Users currentUser){
        ChatRoom newRoom = roomService.createRoom(request.getRoomName(), currentUser.getUsername());
        return ResponseEntity.ok(newRoom);
    }

    @PostMapping("/{roomId}/add")
    public ResponseEntity<String> addParticipant( @PathVariable UUID roomId, @RequestBody AddUserRequest request, @AuthenticationPrincipal Users currentUser) {

        try {
            roomService.addParticipants(roomId, request.getUsernameToAdd(), currentUser.getUsername());
            return ResponseEntity.ok("User '" + request.getUsernameToAdd() + "' added successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my-rooms")
    public ResponseEntity<List<ChatRoom>> getMyRooms(@AuthenticationPrincipal Users currentUser) {
        List<ChatRoom> rooms = roomService.findRoomsForUser(currentUser.getUsername());
        return ResponseEntity.ok(rooms);
    }

    @PostMapping("/dm/{recipientUsername}")
    public ResponseEntity<ChatRoom> initiateDM(@PathVariable String receiverUsername, @AuthenticationPrincipal Users currentUser){
        if(currentUser.getUsername().equals(receiverUsername)){
            return ResponseEntity.badRequest().build();
        }

        ChatRoom room = roomService.createOrGetPrivateRoom(currentUser.getUsername(), receiverUsername);
        return ResponseEntity.ok(room);
    }
}

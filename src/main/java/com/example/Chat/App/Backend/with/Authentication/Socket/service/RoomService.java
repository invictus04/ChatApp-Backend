package com.example.Chat.App.Backend.with.Authentication.Socket.service;

import com.example.Chat.App.Backend.with.Authentication.Auth.entity.Users;
import com.example.Chat.App.Backend.with.Authentication.Auth.repository.UserRepository;
import com.example.Chat.App.Backend.with.Authentication.Socket.entity.ChatRoom;
import com.example.Chat.App.Backend.with.Authentication.Socket.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RoomService {


    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatRoom createRoom(String roomName, String creatorUsername){
        Users creator = userRepository.findByUsername(creatorUsername).orElseThrow(() -> new RuntimeException("User not found: " + creatorUsername));

        ChatRoom room = new ChatRoom();
        room.setName(roomName);

        room.getParticipants().add(creator);

        return roomRepository.save(room);
    }

    @Transactional
    public void addParticipants(UUID roomId, String usernameToAdd, String currentUserName) {
        ChatRoom room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("room not found: " + roomId));

        boolean isCurrentUserParticipant = room.getParticipants().stream().anyMatch(users -> users.getUsername().equals(currentUserName));

        if (!isCurrentUserParticipant) {
            throw new RuntimeException("Authorization failed: You are not a member of this room.");
        }

        Users userToAdd = userRepository.findByUsername(usernameToAdd).orElseThrow(() -> new RuntimeException("User not found :" + usernameToAdd));

        room.getParticipants().add(userToAdd);

        roomRepository.save(room);
    }

    public List<ChatRoom> findRoomsForUser(String username) {
        return roomRepository.findByParticipants_Username(username);
    }


    public boolean isUserParticipants(String username, String roomIdString) {
        UUID roomId;
        try {
            roomId = UUID.fromString(roomIdString);
        } catch (Exception e) {
            return false;
        }
        return roomRepository.existsByIdAndParticipants_Username(roomId,username);
    }



}

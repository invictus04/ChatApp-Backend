package com.example.Chat.App.Backend.with.Authentication.Socket.repository;

import com.example.Chat.App.Backend.with.Authentication.Socket.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<ChatRoom, UUID> {

    List<ChatRoom> findByParticipants_Username(String username);

    boolean existsByIdAndParticipants_Username(UUID id, String username);
}

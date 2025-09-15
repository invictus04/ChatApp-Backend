package com.example.Chat.App.Backend.with.Authentication.Socket.dto;


import com.example.Chat.App.Backend.with.Authentication.Socket.entity.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private String room;
    private String username;
    private String message;
    private MessageType messageType;
    private String createdAt;
}

package com.example.Chat.App.Backend.with.Authentication.Socket.service;


import com.corundumstudio.socketio.SocketIOClient;
import com.example.Chat.App.Backend.with.Authentication.Socket.dto.MessageDto;
import com.example.Chat.App.Backend.with.Authentication.Socket.entity.Message;
import com.example.Chat.App.Backend.with.Authentication.Socket.entity.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
@Slf4j
public class SocketService {
    private final MessageService messageService;

    public void sendSocketMessage(SocketIOClient senderClient, Message message, String  room, String eventName) {
        try {
            MessageDto DTO = toDTO(message);
//            for (SocketIOClient client : senderClient.getNamespace().getRoomOperations(room).getClients()) {
//                if (!client.getSessionId().equals(senderClient.getSessionId())) {
//                    client.sendEvent(eventName, DTO);
//                }
//            }

            senderClient.getNamespace().getRoomOperations(room).sendEvent(eventName, DTO);

        } catch (Exception e) {
            log.error("Error serializing message", e);
        }
    }

    public void saveAndSendMessages(SocketIOClient socketIOClient, Message message, String room, String eventName) {
        try {
            Message storedMessage = messageService.saveMessage(message);
            log.info("Message saved successfully: {}", storedMessage.getId());
            sendSocketMessage(socketIOClient, storedMessage, room, eventName);
        } catch (Exception e) {
            log.error("Error processing message: ", e);
            socketIOClient.sendEvent("error", "Failed to process message");
        }
    }


    public void saveInfoMessage(SocketIOClient socketIOClient, String message, String room, String eventName) {
        try {
            Message storedMessage = messageService.saveMessage(
                    Message.builder()
                            .messageType(MessageType.SERVER)
                            .message(message)
                            .room(room)
                            .username("system")
                            .createdAt(LocalDateTime.now())
                            .build()
            );
            sendSocketMessage(socketIOClient, storedMessage, room, eventName);
        } catch (Exception e) {
            log.error("Error saving server info message", e);
        }
    }

    private MessageDto toDTO(Message message) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return MessageDto.builder()
                .username(message.getUsername())
                .room(message.getRoom())
                .message(message.getMessage())
                .messageType(message.getMessageType())
                .createdAt(message.getCreatedAt().format(formatter))
                .build();
    }
}
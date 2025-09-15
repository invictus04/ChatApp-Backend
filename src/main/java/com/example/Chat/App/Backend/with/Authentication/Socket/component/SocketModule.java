package com.example.Chat.App.Backend.with.Authentication.Socket.component;


import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.example.Chat.App.Backend.with.Authentication.Socket.constants.Constants;
import com.example.Chat.App.Backend.with.Authentication.Socket.entity.Message;
import com.example.Chat.App.Backend.with.Authentication.Socket.entity.MessageType;
import com.example.Chat.App.Backend.with.Authentication.Socket.service.SocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SocketModule {
    private final SocketIOServer server;
    private final SocketService socketService;

    public SocketModule(SocketIOServer server, SocketService socketService) {
        this.server = server;
        this.socketService = socketService;
        server.addConnectListener(this.onConnected());
        server.addDisconnectListener(this.onDisconnected());
        server.addEventListener("send_message", Message.class, this.onChatReceived());
    }

    private DataListener<Message> onChatReceived() {
        return (senderClient, data, ackSender) -> {
            log.info(data.toString());
//            senderClient.getNamespace().getBroadcastOperations().sendEvent("get_message", data.getMessage());
//            socketService.saveMessage(senderClient, data);
//            socketService.sendSocketMessage(senderClient, data, data.getRoom(), "get_message");
            Message message = Message.builder()
                    .username(data.getUsername())
                    .room(data.getRoom())
                    .message(data.getMessage())
                    .messageType(MessageType.CLIENT)
                    .build();
            socketService.saveAndSendMessages(senderClient, message, data.getRoom(), "get_message");
        };

    }

    private ConnectListener onConnected() {
        return client -> {
            var params = client.getHandshakeData().getUrlParams();

            String room = params.getOrDefault("room", List.of("default"))
                    .stream()
                    .findFirst()
                    .orElse("default");

            String username = params.getOrDefault("username", List.of("anonymous"))
                    .stream()
                    .findFirst()
                    .orElse("anonymous");

            client.joinRoom(room);

            socketService.saveInfoMessage(
                    client,
                    String.format(Constants.WELCOME_MESSAGE, username),
                    room,
                    "get_message"
            );

            log.info("Socket ID[{}] - room[{}] - username [{}] Connected to chat module",
                    client.getSessionId(), room, username);
        };
    }


    private DisconnectListener onDisconnected() {
        return client -> {
            var params = client.getHandshakeData().getUrlParams();

            String room = params.getOrDefault("room", List.of("default"))
                    .stream()
                    .findFirst()
                    .orElse("default");

            String username = params.getOrDefault("username", List.of("anonymous"))
                    .stream()
                    .findFirst()
                    .orElse("anonymous");

            socketService.saveInfoMessage(
                    client,
                    String.format(Constants.DISCONNECT_MESSAGE, username),
                    room,
                    "get_message"
            );

            log.info("Socket ID[{}] - room[{}] - username [{}] disconnected from chat module",
                    client.getSessionId(), room, username);
        };

    }

}

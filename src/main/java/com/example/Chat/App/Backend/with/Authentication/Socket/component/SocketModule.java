package com.example.Chat.App.Backend.with.Authentication.Socket.component;


import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.example.Chat.App.Backend.with.Authentication.Auth.entity.Users;
import com.example.Chat.App.Backend.with.Authentication.Auth.repository.UserRepository;
import com.example.Chat.App.Backend.with.Authentication.Auth.service.JWTService;
import com.example.Chat.App.Backend.with.Authentication.Socket.constants.Constants;
import com.example.Chat.App.Backend.with.Authentication.Socket.entity.Message;
import com.example.Chat.App.Backend.with.Authentication.Socket.entity.MessageType;
import com.example.Chat.App.Backend.with.Authentication.Socket.service.RoomService;
import com.example.Chat.App.Backend.with.Authentication.Socket.service.SocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class SocketModule {
    private final SocketIOServer server;
    private final SocketService socketService;
    private final RoomService roomService;
    private final JWTService jwtService;
    private final UserRepository userRepository;

    public SocketModule(SocketIOServer server, SocketService socketService, RoomService roomService, JWTService jwtService, UserRepository userRepository) {
        this.server = server;
        this.socketService = socketService;
        this.roomService = roomService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        server.addConnectListener(this.onConnected());
        server.addDisconnectListener(this.onDisconnected());
        server.addEventListener("send_message", Message.class, this.onChatReceived());
    }

    private DataListener<Message> onChatReceived() {
        return (senderClient, data, ackSender) -> {
            Users authenticatedUser = senderClient.get("authenticatedUser");

            if (authenticatedUser == null) {
                log.error("Critical: Message received from non-authenticated socket [{}].", senderClient.getSessionId());
                senderClient.sendEvent("error", "Authentication error. Please reconnect.");
                senderClient.disconnect();
                return;
            }

            String roomId = data.getRoom();

            if (roomId == null || !roomService.isUserParticipants(authenticatedUser.getUsername(), roomId)) {
                log.warn("Unauthorized message: User [{}] tried to send to room [{}]", authenticatedUser.getUsername(), roomId);
                senderClient.sendEvent("error", "You are not a member of this room.");
                return;
            }
//            senderClient.getNamespace().getBroadcastOperations().sendEvent("get_message", data.getMessage());
//            socketService.saveMessage(senderClient, data);
//            socketService.sendSocketMessage(senderClient, data, data.getRoom(), "get_message");

            Message message = Message.builder()
                    // **CRITICAL: Set username from the *authenticated* session, not the payload**
                    .username(authenticatedUser.getUsername())
                    .room(roomId)
                    .message(data.getMessage())
                    .messageType(MessageType.CLIENT)
                    .build();

            socketService.saveAndSendMessages(senderClient, message, roomId, "get_message");


        };

    }

    private ConnectListener onConnected() {
        return client -> {
            String authHeaders = client.getHandshakeData().getHttpHeaders().get("Authorization");
            String token = null;

            if(authHeaders != null && authHeaders.startsWith("Bearer ")){
                token = authHeaders.substring(7);
            } else {
                token = client.getHandshakeData().getSingleUrlParam("token");
            }

            if (token == null) {
                log.warn("Socket ID[{}] failed to connect: Missing authentication token.", client.getSessionId());
                client.sendEvent("error", "Missing authentication token.");
                client.disconnect();
                return;
            }

            String userEmail;
            try {
                userEmail = jwtService.extractUserEmail(token);
            } catch (Exception e){
                log.warn("Socket ID[{}] failed to connect: Invalid token.", client.getSessionId());
                client.sendEvent("error", "Invalid authentication token.");
                client.disconnect();
                return;
            }

            Optional<Users> userOptional = userRepository.findByEmail(userEmail);
            if(userOptional.isEmpty()){
                log.warn("Socket ID[{}] failed to connect: User not found for email [{}].", client.getSessionId(), userEmail);
                client.sendEvent("error", "User not found.");
                client.disconnect();
                return;
            }

            Users authenticatedUser = userOptional.get();
            if (!jwtService.isTokenValid(token, authenticatedUser)) {
                log.warn("Socket ID[{}] failed to connect: Token validation failed for [{}].", client.getSessionId(), userEmail);
                client.sendEvent("error", "Token validation failed.");
                client.disconnect();
                return;
            }

            log.info("Socket ID[{}] - User [{}] authenticated successfully.", client.getSessionId(), authenticatedUser.getUsername());

            client.set("authenticatedUser", authenticatedUser);

            var params = client.getHandshakeData().getUrlParams();

            String roomId = params.getOrDefault("room", List.of(""))
                    .stream()
                    .findFirst()
                    .orElse("");

            if (roomId.isEmpty() || !roomService.isUserParticipants(authenticatedUser.getUsername(), roomId)) {
                log.warn("Socket ID[{}] - User [{}] tried to join unauthorized room [{}]. Disconnecting.",
                        client.getSessionId(), authenticatedUser.getUsername(), roomId);
                client.sendEvent("error", "Unauthorized to join room " + roomId);
                client.disconnect();
                return;
            }

            client.joinRoom(roomId);
            client.set("room", roomId);

            socketService.saveInfoMessage(
                    client,
                    String.format(Constants.WELCOME_MESSAGE, authenticatedUser.getUsername()),
                    roomId,
                    "get_message"
            );

            log.info("Socket ID[{}] - room[{}] - username [{}] Connected.", client.getSessionId(), roomId, authenticatedUser.getUsername());

        };
    }


    private DisconnectListener onDisconnected() {
        return client -> {
//            var params = client.getHandshakeData().getUrlParams();
            Users authenticatedUser = client.get("authenticatedUser");

            String room = client.get("room");
            if (authenticatedUser != null && room != null) {
                socketService.saveInfoMessage(
                        client,
                        String.format(Constants.DISCONNECT_MESSAGE, authenticatedUser.getUsername()),
                        room,
                        "get_message"
                );

                log.info("Socket ID[{}] - room[{}] - username [{}] disconnected.",
                        client.getSessionId(), room, authenticatedUser.getUsername());
            } else {
                log.info("Socket ID[{}] disconnected (was not fully authenticated or in a room).", client.getSessionId());
            }

//            String room = params.getOrDefault("room", List.of(""))
//                    .stream()
//                    .findFirst()
//                    .orElse("");
//
//            String username = params.getOrDefault("username", List.of(""))
//                    .stream()
//                    .findFirst()
//                    .orElse("");
//
//            socketService.saveInfoMessage(
//                    client,
//                    String.format(Constants.DISCONNECT_MESSAGE, username),
//                    room,
//                    "get_message"
//            );
//
//            log.info("Socket ID[{}] - room[{}] - username [{}] disconnected from chat module",
//                    client.getSessionId(), room, username);
        };

    }

}

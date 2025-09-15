package com.example.Chat.App.Backend.with.Authentication.Socket.service;


import com.example.Chat.App.Backend.with.Authentication.Socket.entity.Message;
import com.example.Chat.App.Backend.with.Authentication.Socket.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public List<Message> getMessage(String room) {
        return messageRepository.findAllByRoom(room);
    }

    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

}

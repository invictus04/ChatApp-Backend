package com.example.Chat.App.Backend.with.Authentication.Socket.controller;


import com.example.Chat.App.Backend.with.Authentication.Socket.entity.Message;
import com.example.Chat.App.Backend.with.Authentication.Socket.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/{room}")
    public ResponseEntity<List<Message>> getMesssage(@PathVariable String room) {
        return ResponseEntity.ok(messageService.getMessage(room));
    }
}

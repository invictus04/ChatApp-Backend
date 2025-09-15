package com.example.Chat.App.Backend.with.Authentication.Auth.service;


import com.example.Chat.App.Backend.with.Authentication.Auth.entity.Users;
import com.example.Chat.App.Backend.with.Authentication.Auth.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<Users> allUsers() {
        return userRepository.findAll();
    }
}

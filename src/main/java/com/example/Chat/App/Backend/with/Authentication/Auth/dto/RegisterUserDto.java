package com.example.Chat.App.Backend.with.Authentication.Auth.dto;

import lombok.Data;

@Data
public class RegisterUserDto {
    private String email;
    private String password;
    private String username;
}

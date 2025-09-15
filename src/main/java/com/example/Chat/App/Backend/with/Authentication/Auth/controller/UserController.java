package com.example.Chat.App.Backend.with.Authentication.Auth.controller;


import com.example.Chat.App.Backend.with.Authentication.Auth.entity.Users;
import com.example.Chat.App.Backend.with.Authentication.Auth.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test")
    public @ResponseBody String welcome() {
        System.out.println(">>> Inside /users/test controller");
        return "Welcome to API";
    }

    @GetMapping("/me")
    public ResponseEntity<?> authenticateUser() {
        System.out.println("This method inside controller");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        Object principal = authentication.getPrincipal();
        System.out.println(principal);
        if (principal instanceof Users users) {
            return ResponseEntity.ok(
                    Map.of(
                            "username", users.getUsername(),
                            "roles", users.getAuthorities()
                    )
            );
        }
        return ResponseEntity.badRequest().body("Unexpected principal type");
    }

    @GetMapping("/")
    public ResponseEntity<List<Users>> allUsers() {
        List<Users> users = userService.allUsers();
        System.out.println(users.size());
        return ResponseEntity.ok(users);
    }
}

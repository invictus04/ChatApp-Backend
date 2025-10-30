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
        System.out.println("--- INSIDE /me CONTROLLER: START ---");
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            System.out.println("--- 1. Got Authentication object.");

            if (authentication == null) {
                System.out.println("--- 2. Authentication object is NULL.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Auth is null");
            }

            System.out.println("--- 2. Authentication object is NOT null.");
            System.out.println("--- 3. Is Authenticated? " + authentication.isAuthenticated());

            if (!authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Not authenticated");
            }

            Object principal = authentication.getPrincipal();
            System.out.println("--- 4. Got Principal object.");

            if (principal == null) {
                System.out.println("--- 5. Principal object is NULL.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Principal is null");
            }

            System.out.println("--- 5. Principal object class: " + principal.getClass().getName());

            if (principal instanceof Users) {
                Users users = (Users) principal;
                System.out.println("--- 6. Principal is instance of Users: " + users.getUsername());
                return ResponseEntity.ok(
                        Map.of(
                                "username", users.getUsername(),
                                "roles", users.getAuthorities()
                        )
                );
            }

            System.out.println("--- 6. FAILED 'instanceof' CHECK.");
            return ResponseEntity.badRequest().body("Unexpected principal type: " + principal.getClass().getName());

        } catch (Exception e) {
            System.out.println("!!! CRITICAL EXCEPTION in /me controller: " + e.getMessage());
            e.printStackTrace(); // This will print the full error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in controller: " + e.getMessage());
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<Users>> allUsers() {
        List<Users> users = userService.allUsers();
        System.out.println(users.size());
        return ResponseEntity.ok(users);
    }
}

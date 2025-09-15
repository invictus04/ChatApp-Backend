package com.example.Chat.App.Backend.with.Authentication.Auth.controller;


import com.example.Chat.App.Backend.with.Authentication.Auth.dto.LoginUserDto;
import com.example.Chat.App.Backend.with.Authentication.Auth.dto.RegisterUserDto;
import com.example.Chat.App.Backend.with.Authentication.Auth.dto.VerifyUserDto;
import com.example.Chat.App.Backend.with.Authentication.Auth.entity.Users;
import com.example.Chat.App.Backend.with.Authentication.Auth.responses.LoginResponse;
import com.example.Chat.App.Backend.with.Authentication.Auth.service.AuthenticationService;
import com.example.Chat.App.Backend.with.Authentication.Auth.service.JWTService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JWTService jwtService;

    private final AuthenticationService authenticationService;

    public AuthController(JWTService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @GetMapping()
    public String welcomeText() {
        return "Welcome to the Authentication App";
    }

    @PostMapping("/signup")
    public ResponseEntity<Users> register(@RequestBody RegisterUserDto registerUserDto) {
        Users registeredUser = authenticationService.signUp(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginUserDto loginUserDto) {
        Users authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        try {
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Account Verfied Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification Code Sent");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

package com.example.Chat.App.Backend.with.Authentication.Auth.service;


import com.example.Chat.App.Backend.with.Authentication.Auth.dto.LoginUserDto;
import com.example.Chat.App.Backend.with.Authentication.Auth.dto.RegisterUserDto;
import com.example.Chat.App.Backend.with.Authentication.Auth.dto.VerifyUserDto;
import com.example.Chat.App.Backend.with.Authentication.Auth.entity.Users;
import com.example.Chat.App.Backend.with.Authentication.Auth.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final EmailService emailService;


    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    public Users signUp(RegisterUserDto userDto) {
        Users user = new Users(userDto.getUsername(), userDto.getEmail(), passwordEncoder.encode(userDto.getPassword()));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        sendVerificationEmail(user);
        System.out.println("User is Saved");
        return userRepository.save(user);
    }

    public Users authenticate(LoginUserDto loginUserDto) {
        Users user = userRepository.findByEmail(loginUserDto.getEmail()).orElseThrow(() -> new RuntimeException("User not Found"));
        if (!user.isEnabled()) {
            throw new RuntimeException("Account not Verified. Please verify your account");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserDto.getEmail(),
                        loginUserDto.getPassword()
                )
        );

        return user;

    }


    public void verifyUser(VerifyUserDto userDto) {
        Optional<Users> optionalUser = userRepository.findByEmail(userDto.getEmail());
        System.out.println(optionalUser.isPresent());
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification Code has Expired");
            }
            System.out.println(user.getVerificationCode() + " " + userDto.getVerification());
            if (user.getVerificationCode().equals(userDto.getVerification())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid Verification Code");
            }
        } else {
            throw new RuntimeException("User is not Found");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<Users> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    private void sendVerificationEmail(Users user) {
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage =
                "<!doctype html><html lang=\"en\"><head>"
                        + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
                        + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                        + "<title>Welcome to Chat app</title>"
                        + "<style>@media (prefers-color-scheme: dark){body,.email-bg{background:#0b1220!important}.card{background:#0f1629!important;color:#e5e7eb!important}.muted{color:#94a3b8!important}.code{background:#0b1220!important;color:#93c5fd!important;border-color:#1f2a44!important}}"
                        + "@media (max-width:600px){.container{width:100%!important;padding:16px!important}.card{padding:20px!important}}</style>"
                        + "</head><body class=\"email-bg\" style=\"margin:0;padding:0;background:#f4f6fb;\">"
                        + "<div style=\"display:none;max-height:0;overflow:hidden;opacity:0;\">Your verification code for Chat app</div>"
                        + "<table role=\"presentation\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" style=\"background:#f4f6fb;\"><tr><td align=\"center\" style=\"padding:32px 12px;\">"
                        + "<table role=\"presentation\" class=\"container\" width=\"560\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" style=\"width:560px;max-width:560px;background:transparent;\">"
                        + "<tr><td align=\"center\" style=\"padding-bottom:20px;\"><div style=\"font-family:Arial,Helvetica,sans-serif;font-size:20px;font-weight:700;color:#0f172a;\">"
                        + "Chat <span style=\"color:#2563eb;\">app</span></div></td></tr>"
                        + "<tr><td><table role=\"presentation\" class=\"card\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" style=\"background:#ffffff;border-radius:16px;box-shadow:0 10px 30px rgba(18,38,63,0.08);overflow:hidden;\">"
                        + "<tr><td style=\"height:6px;background:linear-gradient(90deg,#6366f1,#3b82f6,#22c55e);\"></td></tr>"
                        + "<tr><td style=\"padding:28px 28px 10px 28px;font-family:Arial,Helvetica,sans-serif;color:#0f172a;\"><h1 style=\"margin:0 0 8px 0;font-size:22px;line-height:1.3;font-weight:800;letter-spacing:-0.2px;\">Welcome to Chat app</h1>"
                        + "<p class=\"muted\" style=\"margin:0;color:#475569;font-size:14px;line-height:1.6;\">Use the verification code below to continue. This code will expire shortly.</p></td></tr>"
                        + "<tr><td style=\"padding:18px 28px 8px 28px;\"><div style=\"font-family:Arial,Helvetica,sans-serif;font-size:12px;color:#64748b;margin-bottom:6px;\">Verification Code</div>"
                        + "<div class=\"code\" style=\"font-family:Consolas,Menlo,Monaco,monospace;font-size:22px;letter-spacing:4px;font-weight:700;color:#1d4ed8;background:#f1f5f9;border:1px solid #e2e8f0;border-radius:12px;padding:14px 16px;text-align:center;\">"
                        + "{{VERIFICATION_CODE}}</div></td></tr>"
                        + "<tr><td style=\"padding:10px 28px 22px 28px;\"><p style=\"margin:0;font-family:Arial,Helvetica,sans-serif;font-size:13px;color:#475569;line-height:1.65;\">If you didn’t request this, you can safely ignore this email.</p></td></tr>"
                        + "<tr><td style=\"padding:14px 28px 26px 28px;border-top:1px solid #eaeef5;\"><p class=\"muted\" style=\"margin:0;font-family:Arial,Helvetica,sans-serif;font-size:12px;color:#94a3b8;line-height:1.6;\">This is a system-generated email; no reply is needed.</p></td></tr>"
                        + "</table></td></tr>"
                        + "<tr><td align=\"center\" style=\"padding:16px 8px 0 8px;\"><p class=\"muted\" style=\"margin:0;font-family:Arial,Helvetica,sans-serif;font-size:11px;color:#94a3b8;\">© Chat app</p></td></tr>"
                        + "</table></td></tr></table></body></html>";

        htmlMessage = htmlMessage.replace("{{VERIFICATION_CODE}}", verificationCode);

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}

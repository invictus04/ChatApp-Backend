package com.example.Chat.App.Backend.with.Authentication.Auth.repository;


import com.example.Chat.App.Backend.with.Authentication.Auth.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    Optional<Users> findByUsername(String username);

    Optional<Users> findByVerificationCode(String verificationCode);
}

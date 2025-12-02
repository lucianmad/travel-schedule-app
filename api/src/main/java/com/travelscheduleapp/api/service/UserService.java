package com.travelscheduleapp.api.service;

import com.travelscheduleapp.api.dto.AuthResponse;
import com.travelscheduleapp.api.dto.LoginRequest;
import com.travelscheduleapp.api.dto.RegisterRequest;
import com.travelscheduleapp.api.exception.BadRequestException;
import com.travelscheduleapp.api.mapper.UserMapper;
import com.travelscheduleapp.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AuthResponse register(RegisterRequest request) {
        if (!Objects.equals(request.password(), request.confirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        if (userRepository.findByEmail(request.email()) != null) {
            throw new BadRequestException("Email already taken");
        }

        if (userRepository.findByUsername(request.username()) != null) {
            throw new BadRequestException("Username already taken");
        }

        var user = userMapper.toEntity(request);

        String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        user.setPasswordHash(hashedPassword);

        var savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.email());
        if (user == null) {
            throw new BadRequestException("Invalid credentials");
        }

        if (!BCrypt.checkpw(request.password(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid credentials");
        }

        return userMapper.toDto(user);
    }
}

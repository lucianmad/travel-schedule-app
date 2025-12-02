package com.travelscheduleapp.api.mapper;

import com.travelscheduleapp.api.dto.AuthResponse;
import com.travelscheduleapp.api.dto.RegisterRequest;
import com.travelscheduleapp.api.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(RegisterRequest request) {
        var user = new User();
        user.setEmail(request.email());
        user.setUsername(request.username());
        return user;
    }

    public AuthResponse toDto(User user) {
        if (user == null) {
            return null;
        }

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername()
        );
    }
}

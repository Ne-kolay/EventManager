package com.example.event.manager.user.mapper;

import com.example.event.manager.user.domain.User;
import com.example.event.manager.user.dto.*;
import com.example.event.manager.user.entity.UserEntity;
import com.example.event.manager.common.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        return new User(
                entity.getId(),
                entity.getLogin(),
                entity.getPassword(),
                entity.getRole(),
                entity.getAge()
        );
    }

    public UserEntity toEntity(User domain) {
        if (domain == null) return null;

        return new UserEntity(
                domain.getId(),
                domain.getLogin(),
                domain.getPassword(),
                domain.getRole(),
                domain.getAge()
        );
    }

    public UserDTO toUserDTO(User domain) {
        if (domain == null) return null;

        return new UserDTO(
                domain.getId(),
                domain.getLogin(),
                domain.getAge(),
                domain.getRole()
        );
    }

    public User fromRegistrationRequest(UserRegistrationRequestDTO dto) {
        return new User(
                null,
                dto.login(),
                dto.password(),
                Role.USER,
                dto.age()
        );
    }

    public UserRegistrationResponseDTO toRegistrationResponse(User domain) {
        if (domain == null) return null;

        return new UserRegistrationResponseDTO(
                domain.getId(),
                domain.getLogin(),
                domain.getAge(),
                domain.getRole()
        );
    }

    public JwtResponse toJwtResponse(String token) {
        return new JwtResponse(
                token
        );
    }
}

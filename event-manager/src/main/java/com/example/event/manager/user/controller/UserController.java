package com.example.event.manager.user.controller;

import com.example.event.manager.user.domain.User;
import com.example.event.manager.user.dto.UserCredentials;
import com.example.event.manager.user.dto.JwtResponse;
import com.example.event.manager.user.dto.UserDTO;
import com.example.event.manager.user.dto.UserRegistrationRequestDTO;
import com.example.event.manager.user.dto.UserRegistrationResponseDTO;
import com.example.event.manager.user.mapper.UserMapper;
import com.example.event.manager.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService,
                          UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<UserRegistrationResponseDTO> createUser(
            @Valid @RequestBody UserRegistrationRequestDTO request
    ) {
        User userToRegister = userMapper.fromRegistrationRequest(request);
        User registeredUser = userService.register(userToRegister);

        UserRegistrationResponseDTO response =
                userMapper.toRegistrationResponse(registeredUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtResponse> auth(
            @Valid @RequestBody UserCredentials credentials
    ) {
        String token = userService.login(credentials.login(), credentials.password());
        JwtResponse response = userMapper.toJwtResponse(token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(userMapper.toUserDTO(user));
    }

}

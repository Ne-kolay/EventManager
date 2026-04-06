package com.example.event.manager.user.service;

import com.example.event.manager.exceptions.InvalidCredentialsException;
import com.example.event.manager.exceptions.LoginAlreadyExistsException;
import com.example.event.manager.security.JwtUtil;
import com.example.event.manager.user.domain.User;
import com.example.event.manager.user.entity.UserEntity;
import com.example.event.manager.user.mapper.UserMapper;
import com.example.event.manager.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,
                       JwtUtil jwtUtil,
                       PasswordEncoder passwordEncoder,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public User register(User user) {
        if (isUserExistByLogin(user.getLogin())) {
            throw new LoginAlreadyExistsException(user.getLogin());
        }
        UserEntity userToSave = userMapper.toEntity(user);
        userToSave.setPassword(passwordEncoder.encode(userToSave.getPassword()));
        UserEntity saved = userRepository.save(userToSave);
        return userMapper.toDomain(saved);
    }

    public String login(String login, String rawPassword) {

        UserEntity entity = userRepository.findByLogin(login)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(rawPassword, entity.getPassword())) {
            throw new InvalidCredentialsException();
        }

        User domain = userMapper.toDomain(entity);
        return jwtUtil.generateToken(domain);
    }

    public User getUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User with id " + id + " does not exist"));
        return userMapper.toDomain(userEntity);
    }

    public User getUserByLogin(String login) {
        UserEntity userEntity = userRepository.findByLogin(login).orElseThrow(
                () -> new EntityNotFoundException("User with login " + login + " does not exist"));
        return userMapper.toDomain(userEntity);
    }

    public boolean isUserExistByLogin(String login) {
        return userRepository.existsByLogin(login);
    }
}

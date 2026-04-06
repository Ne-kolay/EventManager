package com.example.event.manager.user.initializer;

import com.example.event.manager.common.Role;
import com.example.event.manager.user.domain.User;
import com.example.event.manager.user.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DefaultUserInitializer {

    private final UserService userService;

    public DefaultUserInitializer(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void initUsers() {
        if (!userService.isUserExistByLogin("admin")) {
            User admin = new User(null, "admin", "admin", Role.ADMIN, 18);
            userService.register(admin);
        }
        if (!userService.isUserExistByLogin("user")) {
            User user = new User(null, "user", "user", Role.USER, 18);
            userService.register(user);
        }
    }
}

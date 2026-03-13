package com.example.event.manager.user.domain;

import com.example.event.manager.common.Role;

public class User {

    private Long id;
    private String login;
    private String password;
    private Role role;
    private int age;

    public User() {
    }

    public User(Long id,
                String login,
                String password,
                Role role,
                int age) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.role = role;
        this.age = age;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}

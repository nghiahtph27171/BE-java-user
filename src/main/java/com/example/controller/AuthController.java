package com.example.usermanagement.controller;

import com.example.usermanagement.dto.UserRequest;
import com.example.usermanagement.service.AuthService;

import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody UserRequest request) {
        String token = authService.login(
                request.getUsername(),
                request.getPassword()
        );
        return Map.of("token", token);
    }
}

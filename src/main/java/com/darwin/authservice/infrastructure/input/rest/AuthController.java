package com.darwin.authservice.infrastructure.input.rest;

import com.darwin.authservice.application.dto.JwtResponse;
import com.darwin.authservice.application.dto.LoginRequest;
import com.darwin.authservice.application.dto.UserResponse;
import com.darwin.authservice.infrastructure.security.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final IAuthService authService;

    @PostMapping("/login")
    public Mono<JwtResponse> login(@RequestBody @Validated LoginRequest loginRequest) {
        return authService.authenticate(loginRequest);
    }

    @GetMapping("/me")
    public Mono<UserResponse> me() {
        return authService.me();
    }

}

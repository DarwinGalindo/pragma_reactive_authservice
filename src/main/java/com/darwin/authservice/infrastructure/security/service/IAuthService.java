package com.darwin.authservice.infrastructure.security.service;

import com.darwin.authservice.application.dto.JwtResponse;
import com.darwin.authservice.application.dto.LoginRequest;
import com.darwin.authservice.application.dto.UserResponse;
import reactor.core.publisher.Mono;

public interface IAuthService {
    Mono<JwtResponse> authenticate(LoginRequest loginRequest);

    Mono<UserResponse> me();
}

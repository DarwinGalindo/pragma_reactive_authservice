package com.darwin.authservice.infrastructure.security.service;

import com.darwin.authservice.application.dto.JwtResponse;
import com.darwin.authservice.application.dto.LoginRequest;
import com.darwin.authservice.application.dto.UserResponse;
import com.darwin.authservice.infrastructure.exception.InvalidCredentialsException;
import com.darwin.authservice.infrastructure.r2dbc.repository.IUserRepository;
import com.darwin.authservice.infrastructure.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public Mono<JwtResponse> authenticate(LoginRequest loginRequest) {
        return userRepository.findByEmail(loginRequest.getEmail())
                .map(user -> {
                    if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                        var usernamePasswordAuthenticationToken =
                                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                                        loginRequest.getPassword(), List.of(new SimpleGrantedAuthority(user.getRole())));
                        String jwt = jwtProvider.generateToken(usernamePasswordAuthenticationToken);
                        return new JwtResponse(jwt);
                    }
                    throw new InvalidCredentialsException();
                })
                .switchIfEmpty(Mono.error(new InvalidCredentialsException()));
    }

    @Override
    public Mono<UserResponse> me() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (String) securityContext.getAuthentication().getPrincipal())
                .flatMap(userRepository::findByEmail)
                .map(userEntity -> new UserResponse(userEntity.getId(), userEntity.getEmail(), userEntity.getRole()));
    }
}

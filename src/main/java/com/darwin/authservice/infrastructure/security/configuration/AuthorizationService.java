package com.darwin.authservice.infrastructure.security.configuration;

import com.darwin.authservice.infrastructure.r2dbc.repository.IUserRepository;
import com.darwin.authservice.infrastructure.security.jwt.JwtProvider;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AuthorizationService implements ReactiveAuthenticationManager {
    private final IUserRepository userRepository;
    private final JwtProvider jwtProvider;

    public AuthorizationService(IUserRepository userRepository, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = (String) authentication.getCredentials();

        if (!jwtProvider.validateToken(token)) {
            return Mono.empty();
        }
        String username = jwtProvider.getUsernameFromToken(token);

        return userRepository.findByEmail(username)
                .map(user -> new UsernamePasswordAuthenticationToken(
                        user.getEmail(), null, List.of(new SimpleGrantedAuthority(user.getRole()))));
    }
}

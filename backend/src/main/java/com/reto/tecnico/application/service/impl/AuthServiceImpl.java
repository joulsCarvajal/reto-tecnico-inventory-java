package com.reto.tecnico.application.service.impl;

import com.reto.tecnico.application.dto.request.LoginRequest;
import com.reto.tecnico.application.dto.response.AuthResponse;
import com.reto.tecnico.infrastructure.persistence.entity.UsuarioEntity;
import com.reto.tecnico.infrastructure.persistence.repository.UsuarioRepository;
import com.reto.tecnico.infrastructure.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioRepository usuarioRepository;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getPassword())
        );

        String token = tokenProvider.generateToken(auth);

        UsuarioEntity usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow();

        return AuthResponse.builder()
                .token(token)
                .correo(usuario.getCorreo())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol().name())
                .expiresIn(jwtExpirationMs)
                .build();
    }
}

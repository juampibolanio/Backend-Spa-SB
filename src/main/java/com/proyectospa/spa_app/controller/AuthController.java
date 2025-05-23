package com.proyectospa.spa_app.controller;

import com.proyectospa.spa_app.dto.LoginDTO;
import com.proyectospa.spa_app.dto.RegistroUsuarioDTO;
import com.proyectospa.spa_app.model.Usuario;
import com.proyectospa.spa_app.security.JwtUtil;
import com.proyectospa.spa_app.service.AuthService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                    .orElse("UNKNOWN");

            String jwt = jwtUtil.generateJwtToken(request.getEmail(), role);

            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody RegistroUsuarioDTO usuarioDTO) {
        if (authService.buscarPorEmail(usuarioDTO.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("El email ya está registrado");
        }

        Usuario nuevoUsuario = authService.registrarUsuario(usuarioDTO);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @Data
    private static class JwtResponse {
        private final String token;
    }
}
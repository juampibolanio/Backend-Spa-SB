package com.proyectospa.spa_app.controller;

import com.proyectospa.spa_app.dto.LoginDTO;
import com.proyectospa.spa_app.dto.RegistroUsuarioDTO;
import com.proyectospa.spa_app.model.Usuario;
import com.proyectospa.spa_app.security.JwtUtil;
import com.proyectospa.spa_app.security.UserDetailsImpl;
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

            // Obtenemos el usuario logueado
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            Integer userId = userDetails.getId(); // <-- ID del usuario
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                    .orElse("UNKNOWN");

            // Generamos el token incluyendo todos los datos necesarios
            String jwt = jwtUtil.generateJwtToken(
                userDetails.getUsername(), 
                role, 
                userId, 
                userDetails.getDni(),
                userDetails.getApellido(),
                userDetails.getNombre()
            );

            // Respuesta más completa con información del usuario
            LoginResponse response = new LoginResponse(
                jwt,
                "Bearer",
                userId,
                userDetails.getUsername(),
                role,
                userDetails.getNombre(),
                userDetails.getApellido(),
                userDetails.getDni()
            );

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario deshabilitado");
        } catch (LockedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario bloqueado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody RegistroUsuarioDTO usuarioDTO) {
        try {
            if (authService.buscarPorEmail(usuarioDTO.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body("El email ya está registrado");
            }

            Usuario nuevoUsuario = authService.registrarUsuario(usuarioDTO);
            return ResponseEntity.ok(nuevoUsuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar usuario: " + e.getMessage());
        }
    }

    // Clases DTO para las respuestas
    @Data
    private static class JwtResponse {
        private final String token;
    }

    @Data
    private static class LoginResponse {
        private String token;
        private String type = "Bearer";
        private Integer id;
        private String email;
        private String rol;
        private String nombre;
        private String apellido;
        private String dni;

        public LoginResponse(String token, String type, Integer id, String email, String rol, 
                           String nombre, String apellido, String dni) {
            this.token = token;
            this.type = type;
            this.id = id;
            this.email = email;
            this.rol = rol;
            this.nombre = nombre;
            this.apellido = apellido;
            this.dni = dni;
        }
    }
}
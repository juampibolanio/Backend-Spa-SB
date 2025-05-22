package com.proyectospa.spa_app.service;

import com.proyectospa.spa_app.dto.RegistroUsuarioDTO;
import com.proyectospa.spa_app.model.Usuario;
import com.proyectospa.spa_app.repository.UsuarioRepository;
import com.proyectospa.spa_app.model.Rol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario registrarUsuario(RegistroUsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setDni(dto.getDni());
        usuario.setActivo(true);
        usuario.setRol(dto.getRol() != null ? dto.getRol() : Rol.CLIENTE); // Por defecto CLIENTE

        return usuarioRepository.save(usuario);
    }
}

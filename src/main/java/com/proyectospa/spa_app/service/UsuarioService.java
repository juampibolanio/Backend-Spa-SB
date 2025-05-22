package com.proyectospa.spa_app.service;

import com.proyectospa.spa_app.model.Usuario;
import com.proyectospa.spa_app.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepo;

    public Usuario guardarUsuario(Usuario usuario) {
        return usuarioRepo.save(usuario);
    }

    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepo.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepo.findByEmail(email);
    }

    public List<Usuario> listarPorRol(String rol) {
        return usuarioRepo.findByRol(Enum.valueOf(com.proyectospa.spa_app.model.Rol.class, rol.toUpperCase()));
    }

    public List<Usuario> listarTodos() {
        return usuarioRepo.findAll();
    }
}
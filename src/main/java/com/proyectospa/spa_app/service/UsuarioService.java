package com.proyectospa.spa_app.service;

import com.proyectospa.spa_app.dto.UsuarioDTO;
import com.proyectospa.spa_app.model.Rol;
import com.proyectospa.spa_app.model.Usuario;
import com.proyectospa.spa_app.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public UsuarioDTO obtenerUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
        return new UsuarioDTO(usuario);
    }

    public List<UsuarioDTO> listarProfesionales() {
        return usuarioRepo.findByRol(com.proyectospa.spa_app.model.Rol.PROFESIONAL)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    public List<UsuarioDTO> listarClientes() {
        Rol clienteRol = com.proyectospa.spa_app.model.Rol.CLIENTE;
        List<Usuario> clientes = usuarioRepo.findByRol(clienteRol);
        return clientes.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private UsuarioDTO convertirADTO(Usuario usuario) {
        return new UsuarioDTO(usuario);
    }

    public void eliminarUsuario(Integer id) {
        Optional<Usuario> usuario = usuarioRepo.findById(id);
        if (usuario.isPresent()) {
            usuarioRepo.delete(usuario.get());
        } else {
            throw new UsernameNotFoundException("Usuario no encontrado con ID: " + id);
        }
    }

    public Usuario activarUsuario(Integer id) {
        Usuario usuario = usuarioRepo.findById(id).orElseThrow();
        usuario.setActivo(true);
        return usuarioRepo.save(usuario);
    }

    public Usuario desactivarUsuario(Integer id) {
        Usuario usuario = usuarioRepo.findById(id).orElseThrow();
        usuario.setActivo(false);
        return usuarioRepo.save(usuario);
    }

}
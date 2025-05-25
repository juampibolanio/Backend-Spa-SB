package com.proyectospa.spa_app.controller;

import com.proyectospa.spa_app.dto.UsuarioDTO;
import com.proyectospa.spa_app.model.Usuario;
import com.proyectospa.spa_app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registrar")
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.guardarUsuario(usuario);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Integer id) {
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<Usuario>> listarPorRol(@PathVariable String rol) {
        List<Usuario> usuarios = usuarioService.listarPorRol(rol);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Usuario>> listarTodos() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioDTO> obtenerUsuarioPorEmail(@PathVariable String email) {
        UsuarioDTO usuario = usuarioService.obtenerUsuarioPorEmail(email);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/profesionales")
    public ResponseEntity<List<UsuarioDTO>> listarProfesionales() {
        List<UsuarioDTO> profesionales = usuarioService.listarProfesionales();
        return ResponseEntity.ok(profesionales);
    }

    @GetMapping("/clientes")
    public ResponseEntity<List<UsuarioDTO>> listarClientes() {
        List<UsuarioDTO> clientes = usuarioService.listarClientes();
        return ResponseEntity.ok(clientes);
    }
}

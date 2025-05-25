package com.proyectospa.spa_app.repository;

import com.proyectospa.spa_app.model.Rol;
import com.proyectospa.spa_app.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByRol(Rol rol);
    boolean existsByEmail(String email);



}

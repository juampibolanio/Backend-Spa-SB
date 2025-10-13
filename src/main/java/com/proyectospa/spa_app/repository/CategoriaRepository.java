package com.proyectospa.spa_app.repository;

import com.proyectospa.spa_app.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    boolean existsByNombre(String nombre);
}

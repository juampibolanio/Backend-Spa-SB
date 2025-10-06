package com.proyectospa.spa_app.repository;

import com.proyectospa.spa_app.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    //buscar por nombre si algún día querés validar duplicados
    boolean existsByNombre(String nombre);
}

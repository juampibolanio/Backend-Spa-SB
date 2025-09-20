package com.proyectospa.spa_app.repository;

import com.proyectospa.spa_app.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findbyId(Integer productoID);

}


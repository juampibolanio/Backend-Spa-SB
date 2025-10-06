package com.proyectospa.spa_app.repository;

import com.proyectospa.spa_app.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
}

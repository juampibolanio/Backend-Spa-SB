package com.proyectospa.spa_app.repository;

import com.proyectospa.spa_app.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicioRepository extends JpaRepository<Servicio, Integer> {
    List<Servicio> findByProfesionalId(Integer profesionalId);
}

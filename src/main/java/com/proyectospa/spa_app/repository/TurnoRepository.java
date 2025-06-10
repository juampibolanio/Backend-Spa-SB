package com.proyectospa.spa_app.repository;

import com.proyectospa.spa_app.model.EstadoTurno;
import com.proyectospa.spa_app.model.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TurnoRepository extends JpaRepository<Turno, Integer> {
    List<Turno> findByProfesionalIdAndFecha(Integer profesionalId, LocalDate fecha);
    List<Turno> findByFecha(LocalDate fecha);
    List<Turno> findByEstado(EstadoTurno estado);
    List<Turno> findByProfesionalIdAndEstado(Integer profesionalId, EstadoTurno estado);
    List<Turno> findByProfesionalId(Integer profesionalId);
    List<Turno> findByClienteId(Integer clienteId);
    List<Turno> findByClienteIdOrderByFechaDesc(Long clienteId);
    List<Turno> findByProfesionalIdAndClienteId(Integer profesionalId, Integer clienteId);
    
}

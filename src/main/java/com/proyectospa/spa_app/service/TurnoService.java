package com.proyectospa.spa_app.service;

import com.proyectospa.spa_app.model.Turno;
import com.proyectospa.spa_app.repository.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TurnoService {

    @Autowired
    private TurnoRepository turnoRepo;

    public Turno crearTurno(Turno turno) {
        // Validación:  esto es porq solo se puede reservar hasta 48 hs antes
        if (turno.getFecha().isBefore(LocalDate.now().plusDays(2))) {
            throw new IllegalArgumentException("La reserva debe hacerse con al menos 48 hs de anticipación.");
        }
        return turnoRepo.save(turno);
    }

    public List<Turno> listarPorFecha(LocalDate fecha) {
        return turnoRepo.findByFecha(fecha);
    }

    public List<Turno> listarPorProfesionalYFecha(Integer profesionalId, LocalDate fecha) {
        return turnoRepo.findByProfesionalIdAndFecha(profesionalId, fecha);
    }

    public List<Turno> listarTodos() {
        return turnoRepo.findAll();
    }
}
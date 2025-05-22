package com.proyectospa.spa_app.controller;

import com.proyectospa.spa_app.model.Turno;
import com.proyectospa.spa_app.service.TurnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    @Autowired
    private TurnoService turnoService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearTurno(@RequestBody Turno turno) {
        try {
            Turno nuevoTurno = turnoService.crearTurno(turno);
            return ResponseEntity.ok(nuevoTurno);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<Turno>> listarPorFecha(@PathVariable String fecha) {
        LocalDate fechaLocal = LocalDate.parse(fecha);
        List<Turno> turnos = turnoService.listarPorFecha(fechaLocal);
        return ResponseEntity.ok(turnos);
    }

    @GetMapping("/profesional/{id}/fecha/{fecha}")
    public ResponseEntity<List<Turno>> listarPorProfesionalYFecha(@PathVariable Integer id, @PathVariable String fecha) {
        LocalDate fechaLocal = LocalDate.parse(fecha);
        List<Turno> turnos = turnoService.listarPorProfesionalYFecha(id, fechaLocal);
        return ResponseEntity.ok(turnos);
    }
}
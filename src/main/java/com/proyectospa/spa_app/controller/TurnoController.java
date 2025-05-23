package com.proyectospa.spa_app.controller;

import com.proyectospa.spa_app.dto.TurnoDTO;
import com.proyectospa.spa_app.model.Servicio;
import com.proyectospa.spa_app.model.Turno;
import com.proyectospa.spa_app.model.Usuario;
import com.proyectospa.spa_app.service.ServicioService;
import com.proyectospa.spa_app.service.TurnoService;
import com.proyectospa.spa_app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    @Autowired
    private TurnoService turnoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ServicioService servicioService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearTurno(@RequestBody TurnoDTO dto) {
        Usuario cliente = usuarioService.buscarPorId(dto.getClienteId()).orElse(null);
        Usuario profesional = usuarioService.buscarPorId(dto.getProfesionalId()).orElse(null);
        Servicio servicio = servicioService.listarTodos()
                .stream()
                .filter(s -> s.getId().equals(dto.getServicioId()))
                .findFirst()
                .orElse(null);

        if (cliente == null || profesional == null || servicio == null) {
            return ResponseEntity.badRequest().body("Cliente, profesional o servicio no encontrado");
        }

        try {
            Turno turno = turnoService.toEntity(dto, cliente, profesional, servicio);
            TurnoDTO creado = turnoService.toDTO(turnoService.crearTurno(turno));
            return ResponseEntity.ok(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<?> listarPorFecha(@PathVariable String fecha) {
        try {
            LocalDate fechaLocal = LocalDate.parse(fecha);
            List<Turno> turnos = turnoService.listarPorFecha(fechaLocal);
            return ResponseEntity.ok(turnoService.toDTOList(turnos));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Formato de fecha inválido. Use yyyy-MM-dd");
        }
    }
    @GetMapping("/profesional/{id}/maniana")
    public ResponseEntity<List<TurnoDTO>> listarTurnosManiana(@PathVariable Integer id) {
        LocalDate maniana = LocalDate.now().plusDays(1);
        List<Turno> turnos = turnoService.listarPorProfesionalYFecha(id, maniana);
        return ResponseEntity.ok(turnoService.toDTOList(turnos));
    }

    @GetMapping("/profesional/{id}")
    public ResponseEntity<List<TurnoDTO>> listarTurnosPorProfesional(@PathVariable Integer id) {
        List<Turno> turnos = turnoService.listarPorProfesional(id);
        return ResponseEntity.ok(turnoService.toDTOList(turnos));
    }

}
package com.proyectospa.spa_app.controller;

import com.proyectospa.spa_app.dto.ClienteResumenDTO;
import com.proyectospa.spa_app.dto.DetalleAtencionDTO;
import com.proyectospa.spa_app.dto.SolicitudTurnosDTO;
import com.proyectospa.spa_app.dto.TurnoDTO;
import com.proyectospa.spa_app.dto.TurnoHistorialDTO;
import com.proyectospa.spa_app.dto.TurnoProfesionalDTO;
import com.proyectospa.spa_app.model.Servicio;
import com.proyectospa.spa_app.model.Turno;
import com.proyectospa.spa_app.model.Usuario;
import com.proyectospa.spa_app.repository.TurnoRepository;
import com.proyectospa.spa_app.service.ServicioService;
import com.proyectospa.spa_app.service.TurnoService;
import com.proyectospa.spa_app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    @Autowired
    private TurnoRepository turnoRepository;

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

    @GetMapping("/profesional")
    public ResponseEntity<List<TurnoProfesionalDTO>> obtenerTurnosDelProfesional(Authentication authentication) {
        String email = authentication.getName();
        List<TurnoProfesionalDTO> turnos = turnoService.obtenerTurnosPorEmail(email);
        return ResponseEntity.ok(turnos);
    }

    @GetMapping("/profesional/{id}")
    public ResponseEntity<List<TurnoProfesionalDTO>> listarTurnosPorProfesional(@PathVariable Integer id) {
        List<Turno> turnos = turnoService.listarPorProfesional(id);
        List<TurnoProfesionalDTO> resultado = turnos.stream().map(turno -> {
            String estado = (turno.getEstado() != null) ? turno.getEstado().toString() : "SIN_ESTADO";
            String metodoPago = (turno.getMetodoPago() != null) ? turno.getMetodoPago().toString() : "NO_ESPECIFICADO";

            return new TurnoProfesionalDTO(
                    turno.getId(),
                    turno.getCliente().getNombre(),
                    turno.getCliente().getApellido(),
                    turno.getCliente().getId(),
                    turno.getServicio().getNombre(),
                    turno.getFecha().toString(),
                    turno.getHoraInicio().toString(),
                    turno.getHoraFin().toString(),
                    estado,
                    turno.isPagado(),
                    turno.isPagoWeb(),
                    metodoPago,
                    turno.getMonto(),
                    turno.getDetalle());
        }).collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/pago-agrupado")
    public ResponseEntity<?> pagarTurnosAgrupados(@RequestBody List<Integer> turnoIds) {
        try {
            List<Turno> turnosPagados = turnoService.procesarPagoAgrupado(turnoIds);
            List<TurnoDTO> dtos = turnoService.toDTOList(turnosPagados);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<TurnoDTO>> obtenerTurnosDelCliente(@PathVariable Integer clienteId,
            Authentication authentication) {
        String emailUsuarioLogueado = authentication.getName();
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuarioLogueado).orElse(null);

        if (usuario == null || !usuario.getId().equals(clienteId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<TurnoDTO> turnos = turnoService.obtenerTurnosPorCliente(clienteId);
        return ResponseEntity.ok(turnos);
    }
    // PENDIENTE ARREGLAR LOS ESTILOS DEL PANEL DE PROFESIONAL, ARREGLAR LO DEL
    // HISTORIAL DE TURNOS, Q SE VEA TMB EL DETALLE,
    // LA LSITA DE TURNOS

    @GetMapping("/cliente/{clienteId}/historial")
    public ResponseEntity<List<TurnoProfesionalDTO>> historialPorCliente(@PathVariable Integer clienteId) {
        List<Turno> turnos = turnoService.historialPorCliente(clienteId);
        List<TurnoProfesionalDTO> resultado = turnos.stream().map(turno -> {
            String estado = (turno.getEstado() != null) ? turno.getEstado().toString() : "SIN_ESTADO";
            String metodoPago = (turno.getMetodoPago() != null) ? turno.getMetodoPago().toString() : "NO_ESPECIFICADO";

            return new TurnoProfesionalDTO(
                    turno.getId(),
                    turno.getCliente().getNombre(),
                    turno.getCliente().getApellido(),
                    turno.getCliente().getId(),
                    turno.getServicio().getNombre(),
                    turno.getFecha().toString(),
                    turno.getHoraInicio().toString(),
                    turno.getHoraFin().toString(),
                    estado,
                    turno.isPagado(),
                    turno.isPagoWeb(),
                    metodoPago,
                    turno.getMonto(),
                    turno.getDetalle());
        }).collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/profesional/{id}/clientes")
    public ResponseEntity<List<ClienteResumenDTO>> listarClientesDeProfesional(@PathVariable Integer id) {
        List<ClienteResumenDTO> clientes = turnoService.obtenerClientesDeProfesional(id);
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/profesional/{profesionalId}/cliente/{clienteId}/historial")
    public ResponseEntity<List<TurnoHistorialDTO>> verHistorialDelCliente(
            @PathVariable Integer profesionalId,
            @PathVariable Integer clienteId) {
        List<TurnoHistorialDTO> historial = turnoService.obtenerHistorialDeCliente(profesionalId, clienteId);
        return ResponseEntity.ok(historial);
    }

    @PutMapping("/{id}/detalle-atencion")
    public ResponseEntity<String> agregarDetalleAtencion(@PathVariable Integer id,
            @RequestBody DetalleAtencionDTO dto) {
        turnoService.agregarDetalleAtencion(id, dto.getDetalle());
        return ResponseEntity.ok("Detalle de atención actualizado correctamente.");
    }

    @PutMapping("/{id}/detalle")
    public ResponseEntity<String> actualizarDetalle(@PathVariable Integer id, @RequestBody String nuevoDetalle) {
        turnoService.actualizarDetalle(id, nuevoDetalle);
        return ResponseEntity.ok("Detalle actualizado correctamente.");
    }

    @PostMapping("/multiples")
    public ResponseEntity<List<TurnoDTO>> registrarMultiplesTurnos(@RequestBody List<SolicitudTurnosDTO> solicitudes) {
        List<TurnoDTO> turnosRegistrados = turnoService.registrarTurnosDesdeSolicitud(solicitudes);
        return ResponseEntity.ok(turnosRegistrados);
    }

}

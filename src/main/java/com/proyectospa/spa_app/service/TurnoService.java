package com.proyectospa.spa_app.service;

import com.proyectospa.spa_app.dto.ClienteResumenDTO;
import com.proyectospa.spa_app.dto.SolicitudTurnosDTO;
import com.proyectospa.spa_app.dto.TurnoDTO;
import com.proyectospa.spa_app.dto.TurnoHistorialDTO;
import com.proyectospa.spa_app.dto.TurnoPorDiaDTO;
import com.proyectospa.spa_app.dto.TurnoProfesionalDTO;
import com.proyectospa.spa_app.model.EstadoTurno;
import com.proyectospa.spa_app.model.MetodoPago;
import com.proyectospa.spa_app.model.Servicio;
import com.proyectospa.spa_app.model.Turno;
import com.proyectospa.spa_app.model.Usuario;
import com.proyectospa.spa_app.repository.TurnoRepository;
import com.proyectospa.spa_app.repository.UsuarioRepository;
import com.proyectospa.spa_app.repository.ServicioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TurnoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TurnoRepository turnoRepo;
    @Autowired
    private EmailService emailService;

    @Autowired
    private ServicioRepository servicioRepository;

    public Turno crearTurno(Turno turno) {
        if (turno.getFecha().isBefore(LocalDate.now().plusDays(2))) {
            throw new IllegalArgumentException("La reserva debe hacerse con al menos 48 hs de anticipación.");
        }

        double precio = turno.getServicio().getPrecio();
        boolean aplicaDescuento = turno.getMetodoPago() == MetodoPago.TARJETA_DEBITO && turno.isPagoWeb();

        if (aplicaDescuento) {
            turno.setMonto(precio * 0.85);
        } else {
            turno.setMonto(precio);
        }

        Turno guardado = turnoRepo.save(turno);

        emailService.enviarComprobante(
                guardado.getCliente().getEmail(),
                "Comprobante de turno",
                generarContenidoHtml(guardado));

        return guardado;
    }

    public List<Turno> listarPorFecha(LocalDate fecha) {
        return turnoRepo.findByFecha(fecha);
    }

    public List<Turno> listarPorProfesionalYFecha(Integer profesionalId, LocalDate fecha) {
        return turnoRepo.findByProfesionalIdAndFecha(profesionalId, fecha);
    }

    public List<Turno> listarPorProfesional(Integer profesionalId) {
        return turnoRepo.findByProfesionalId(profesionalId);
    }

    public List<Turno> listarTodos() {
        return turnoRepo.findAll();
    }

    public List<Turno> listarPorEstado(EstadoTurno estado) {
        return turnoRepo.findByEstado(estado);
    }

    public List<Turno> listarPorProfesionalYEstado(Integer profesionalId, EstadoTurno estado) {
        return turnoRepo.findByProfesionalIdAndEstado(profesionalId, estado);
    }

    public List<TurnoProfesionalDTO> obtenerTurnosPorProfesional(Integer profesionalId) {
        List<Turno> turnos = turnoRepo.findByProfesionalId(profesionalId);
        return turnos.stream().map(turno -> new TurnoProfesionalDTO(
                turno.getId(),
                turno.getCliente().getNombre(),
                turno.getCliente().getApellido(),
                turno.getCliente().getId(),
                turno.getServicio().getNombre(),
                turno.getFecha().toString(),
                turno.getHoraInicio().toString(),
                turno.getHoraFin().toString(),
                turno.getEstado().name(),
                turno.isPagado(),
                turno.isPagoWeb(),
                turno.getMetodoPago() != null ? turno.getMetodoPago().name() : null,
                turno.getMonto(),
                turno.getDetalle())).toList();
    }

    public Turno cambiarEstado(Integer turnoId, EstadoTurno nuevoEstado) {
        Turno turno = turnoRepo.findById(turnoId)
                .orElseThrow(() -> new IllegalArgumentException("Turno no encontrado"));
        turno.setEstado(nuevoEstado);
        return turnoRepo.save(turno);
    }

    public TurnoDTO toDTO(Turno turno) {
        TurnoDTO dto = new TurnoDTO();
        dto.setId(turno.getId());
        if (turno.getCliente() != null)
            dto.setClienteId(turno.getCliente().getId());
        if (turno.getProfesional() != null)
            dto.setProfesionalId(turno.getProfesional().getId());
        if (turno.getServicio() != null)
            dto.setServicioId(turno.getServicio().getId());
        if (turno.getEstado() != null)
            dto.setEstado(turno.getEstado().name());
        dto.setFecha(turno.getFecha());
        dto.setHoraInicio(turno.getHoraInicio());
        dto.setHoraFin(turno.getHoraFin());
        dto.setPagado(turno.isPagado());
        dto.setPagoWeb(turno.isPagoWeb());
        if (turno.getMetodoPago() != null)
            dto.setMetodoPago(turno.getMetodoPago().name());
        dto.setMonto(turno.getMonto());
        dto.setDetalle(turno.getDetalle());
        return dto;
    }

    public Turno toEntity(TurnoDTO dto, Usuario cliente, Usuario profesional, Servicio servicio) {
        Turno turno = new Turno();
        turno.setId(dto.getId());
        turno.setCliente(cliente);
        turno.setProfesional(profesional);
        turno.setServicio(servicio);
        if (dto.getEstado() != null) {
            turno.setEstado(EstadoTurno.valueOf(dto.getEstado()));
        }
        turno.setFecha(dto.getFecha());
        turno.setHoraInicio(dto.getHoraInicio());
        turno.setHoraFin(dto.getHoraFin());
        turno.setPagado(dto.isPagado());
        turno.setPagoWeb(dto.isPagoWeb());
        if (dto.getMetodoPago() != null) {
            turno.setMetodoPago(MetodoPago.valueOf(dto.getMetodoPago()));
        }
        return turno;
    }

    public List<TurnoDTO> toDTOList(List<Turno> turnos) {
        return turnos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public String generarContenidoHtml(Turno turno) {
        return """
                <h2>Comprobante de Turno</h2>
                <p><strong>Cliente:</strong> %s</p>
                <p><strong>Profesional:</strong> %s</p>
                <p><strong>Servicio:</strong> %s</p>
                <p><strong>Fecha:</strong> %s</p>
                <p><strong>Hora:</strong> %s - %s</p>
                <p><strong>Monto:</strong> $%.2f</p>
                """.formatted(
                turno.getCliente().getNombre(),
                turno.getProfesional().getNombre(),
                turno.getServicio().getNombre(),
                turno.getFecha(),
                turno.getHoraInicio(),
                turno.getHoraFin(),
                turno.getMonto());
    }

    public Turno buscarPorId(Integer id) {
        return turnoRepo.findById(id).orElse(null);
    }

    public List<Turno> procesarPagoAgrupado(List<Integer> turnoIds) {
        List<Turno> turnos = turnoRepo.findAllById(turnoIds);

        if (turnos.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron turnos para procesar.");
        }

        LocalDate fecha = turnos.get(0).getFecha();
        Integer clienteId = turnos.get(0).getCliente().getId();

        boolean mismoClienteYFecha = turnos.stream().allMatch(t -> t.getFecha().equals(fecha) &&
                t.getCliente().getId().equals(clienteId));

        if (!mismoClienteYFecha) {
            throw new IllegalArgumentException("Todos los turnos deben ser del mismo cliente y fecha.");
        }

        double total = 0;
        for (Turno t : turnos) {
            double precio = t.getServicio().getPrecio();
            boolean aplicaDescuento = t.getMetodoPago() == MetodoPago.TARJETA_DEBITO && t.isPagoWeb();
            double monto = aplicaDescuento ? precio * 0.85 : precio;

            t.setMonto(monto);
            t.setPagado(true);
            t.setEstado(EstadoTurno.CONFIRMADO);
            turnoRepo.save(t);

            total += monto;
        }

        emailService.enviarComprobante(
                turnos.get(0).getCliente().getEmail(),
                "Comprobante de pago agrupado",
                generarHtmlAgrupado(turnos, total));

        return turnos;
    }

    private String generarHtmlAgrupado(List<Turno> turnos, double total) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>Comprobante de Pago Agrupado</h2>");
        sb.append("<p><strong>Cliente:</strong> ").append(turnos.get(0).getCliente().getNombre()).append("</p>");
        sb.append("<ul>");
        for (Turno t : turnos) {
            sb.append("<li>")
                    .append(t.getServicio().getNombre())
                    .append(" con ").append(t.getProfesional().getNombre())
                    .append(" – ").append(t.getHoraInicio())
                    .append(" a ").append(t.getHoraFin())
                    .append(" – $").append(String.format("%.2f", t.getMonto()))
                    .append("</li>");
        }
        sb.append("</ul>");
        sb.append("<p><strong>Total:</strong> $").append(String.format("%.2f", total)).append("</p>");
        return sb.toString();
    }

    public Map<String, Object> generarReportePagos(LocalDate desde, LocalDate hasta, Integer profesionalId,
            Integer servicioId) {
        List<Turno> turnos = turnoRepo.findAll().stream()
                .filter(t -> !t.getFecha().isBefore(desde) && !t.getFecha().isAfter(hasta))
                .filter(t -> profesionalId == null || t.getProfesional().getId().equals(profesionalId))
                .filter(t -> servicioId == null || t.getServicio().getId().equals(servicioId))
                .toList();

        double totalRecaudado = turnos.stream().mapToDouble(Turno::getMonto).sum();
        int cantidadTurnos = turnos.size();

        Map<String, Object> reporte = new LinkedHashMap<>();
        reporte.put("desde", desde);
        reporte.put("hasta", hasta);
        reporte.put("profesionalId", profesionalId);
        reporte.put("servicioId", servicioId);
        reporte.put("cantidadTurnos", cantidadTurnos);
        reporte.put("totalRecaudado", totalRecaudado);
        reporte.put("detalle", toDTOList(turnos));

        return reporte;
    }

    public TurnoProfesionalDTO mapToTurnoProfesionalDTO(Turno turno) {
        return new TurnoProfesionalDTO(
                turno.getId(),
                turno.getCliente().getNombre(),
                turno.getCliente().getApellido(),
                turno.getCliente().getId(),
                turno.getServicio().getNombre(),
                turno.getFecha().toString(),
                turno.getHoraInicio().toString(),
                turno.getHoraFin().toString(),
                turno.getEstado().name(),
                turno.isPagado(),
                turno.isPagoWeb(),
                turno.getMetodoPago().name(),
                turno.getMonto(),
                turno.getDetalle());
    }

    public List<TurnoProfesionalDTO> obtenerTurnosPorEmail(String email) {
        Usuario profesional = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));

        List<Turno> turnos = turnoRepo.findByProfesionalId(profesional.getId());

        return turnos.stream().map(turno -> new TurnoProfesionalDTO(
                turno.getId(),
                turno.getCliente().getNombre(),
                turno.getCliente().getApellido(),
                turno.getCliente().getId(),
                turno.getServicio().getNombre(),
                turno.getFecha().toString(),
                turno.getHoraInicio().toString(),
                turno.getHoraFin().toString(),
                turno.getEstado().name(),
                turno.isPagado(),
                turno.isPagoWeb(),
                turno.getMetodoPago() != null ? turno.getMetodoPago().name() : null,
                turno.getMonto(),
                turno.getDetalle())).toList();
    }

    public List<TurnoDTO> obtenerTurnosPorCliente(Integer clienteId) {
        List<Turno> turnos = turnoRepo.findByClienteId(clienteId);
        return turnos.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<TurnoDTO> obtenerHistorialPorCliente(Long clienteId) {
        List<Turno> turnos = turnoRepo.findByClienteIdOrderByFechaDesc(clienteId);
        return turnos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ClienteResumenDTO> obtenerClientesDeProfesional(Integer profesionalId) {
        List<Turno> turnos = turnoRepo.findByProfesionalId(profesionalId);

        return turnos.stream()
                .map(turno -> turno.getCliente())
                .distinct() // evita duplicados (necesitás implementar equals/hashCode en Usuario)
                .map(cliente -> new ClienteResumenDTO(
                        cliente.getId(),
                        cliente.getNombre(),
                        cliente.getApellido(),
                        cliente.getEmail()))
                .collect(Collectors.toList());
    }

    public List<TurnoHistorialDTO> obtenerHistorialDeCliente(Integer profesionalId, Integer clienteId) {
        List<Turno> turnos = turnoRepo.findByProfesionalIdAndClienteId(profesionalId, clienteId);

        return turnos.stream().map(turno -> new TurnoHistorialDTO(
                turno.getId(),
                turno.getServicio().getNombre(),
                turno.getFecha().toString(),
                turno.getHoraInicio().toString(),
                turno.getHoraFin().toString(),
                (turno.getEstado() != null) ? turno.getEstado().toString() : "SIN_ESTADO",
                turno.isPagado(),
                (turno.getMetodoPago() != null) ? turno.getMetodoPago().toString() : "NO_ESPECIFICADO",
                turno.getMonto(),
                turno.getDetalle())).collect(Collectors.toList());
    }

    public void agregarDetalleAtencion(Integer turnoId, String detalle) {
        Turno turno = turnoRepo.findById(turnoId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado con ID: " + turnoId));
        turno.setDetalle(detalle);
        turnoRepo.save(turno);
    }

    public List<Turno> historialPorCliente(Integer clienteId) {
        return turnoRepo.findByClienteId(clienteId);
    }

    public void actualizarDetalle(Integer turnoId, String nuevoDetalle) {
        Turno turno = turnoRepo.findById(turnoId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        turno.setDetalle(nuevoDetalle);
        turnoRepo.save(turno);
    }

    public String generarComprobanteHtml(List<Turno> turnos) {
        StringBuilder html = new StringBuilder();
        html.append("<h2>Comprobante de turnos - SPA Sentirse Bien - By Ana Felicidad</h2>");
        html.append("<p>Gracias por reservar con nosotros. Aquí están los detalles de tus turnos:</p>");
        html.append("<ul>");
        for (Turno t : turnos) {
            html.append("<li>")
                    .append("<b>Fecha:</b> ").append(t.getFecha()).append(" - ")
                    .append("<b>Hora:</b> ").append(t.getHoraInicio()).append(" a ").append(t.getHoraFin())
                    .append(" - ")
                    .append("<b>Servicio:</b> ").append(t.getServicio().getNombre()).append(" - ")
                    .append("<b>Profesional:</b> ").append(t.getProfesional().getNombre() + t.getProfesional().getApellido())
                    .append("</li>");
        }
        html.append("</ul>");
        double total = turnos.stream().mapToDouble(Turno::getMonto).sum();
        html.append("<p><b>Total:</b> $" + String.format("%.2f", total) + "</p>");
        return html.toString();
    }

    public List<Turno> convertirDesdeSolicitudTurnosDTO(SolicitudTurnosDTO dto) {
        List<Turno> turnos = new ArrayList<>();

        Usuario cliente = usuarioRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Usuario profesional = usuarioRepository.findById(dto.getProfesionalId())
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));

        for (TurnoPorDiaDTO diaDTO : dto.getTurnosPorDia()) {
            LocalDate fecha = diaDTO.getFecha();
            LocalTime horaInicio = diaDTO.getHoraInicio();
            LocalTime horaFin = diaDTO.getHoraFin(); // lo que elegió el usuario

            for (Integer servicioId : diaDTO.getServicioIds()) {
                Servicio servicio = servicioRepository.findById(servicioId)
                        .orElseThrow(() -> new RuntimeException("Servicio no encontrado: " + servicioId));

                Turno turno = new Turno();
                turno.setCliente(cliente);
                turno.setProfesional(profesional);
                turno.setServicio(servicio);
                turno.setFecha(fecha);
                turno.setHoraInicio(horaInicio);
                turno.setHoraFin(horaFin); // tomamos la hora fin del DTO

                turno.setEstado(EstadoTurno.PENDIENTE);
                turno.setPagoWeb(dto.isPagoWeb());
                turno.setMetodoPago(MetodoPago.valueOf(dto.getMetodoPago().toUpperCase()));
                turno.setPagado(false);

                // Cálculo del monto con posible descuento
                double monto = servicio.getPrecio();
                boolean aplicaDescuento = dto.isPagoWeb()
                        && turno.getMetodoPago() == MetodoPago.TARJETA_DEBITO
                        && LocalDate.now().plusDays(2).isBefore(fecha);

                if (aplicaDescuento) {
                    monto *= 0.85;
                }

                turno.setMonto(monto);
                turno.setDetalle("Sin detalle");

                turnos.add(turno);
            }
        }

        return turnos;
    }

    public List<TurnoDTO> registrarTurnosDesdeSolicitud(List<SolicitudTurnosDTO> solicitudes) {
        List<Turno> todosLosTurnos = new ArrayList<>();

        for (SolicitudTurnosDTO solicitud : solicitudes) {
            List<Turno> turnos = convertirDesdeSolicitudTurnosDTO(solicitud);
            todosLosTurnos.addAll(turnos);
        }

        List<Turno> turnosGuardados = turnoRepo.saveAll(todosLosTurnos);

        // 🚀 Enviar el mail después de guardar
        if (!turnosGuardados.isEmpty()) {
            Usuario cliente = turnosGuardados.get(0).getCliente();
            String emailCliente = cliente.getEmail();
            String asunto = "Comprobante de tus turnos";
            String contenidoHtml = generarComprobanteHtml(turnosGuardados);

            emailService.enviarComprobante(emailCliente, asunto, contenidoHtml);
        }

        return turnosGuardados.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

}
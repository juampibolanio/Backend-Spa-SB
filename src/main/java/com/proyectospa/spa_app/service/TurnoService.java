package com.proyectospa.spa_app.service;

import com.proyectospa.spa_app.dto.TurnoDTO;
import com.proyectospa.spa_app.model.EstadoTurno;
import com.proyectospa.spa_app.model.MetodoPago;
import com.proyectospa.spa_app.model.Servicio;
import com.proyectospa.spa_app.model.Turno;
import com.proyectospa.spa_app.model.Usuario;
import com.proyectospa.spa_app.repository.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TurnoService {

    @Autowired
    private TurnoRepository turnoRepo;

    public Turno crearTurno(Turno turno) {
        if (turno.getFecha().isBefore(LocalDate.now().plusDays(2))) {
            throw new IllegalArgumentException("La reserva debe hacerse con al menos 48 hs de anticipación.");
        }

        double precio = turno.getServicio().getPrecio();
        boolean aplicaDescuento = turno.getMetodoPago() == MetodoPago.TARJETA_DEBITO
                && LocalDate.now().isBefore(turno.getFecha().minusDays(2));

        if (aplicaDescuento) {
            turno.setMonto(precio * 0.85);
        } else {
            turno.setMonto(precio);
        }

        return turnoRepo.save(turno);
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

    public Turno cambiarEstado(Integer turnoId, EstadoTurno nuevoEstado) {
        Turno turno = turnoRepo.findById(turnoId)
                .orElseThrow(() -> new IllegalArgumentException("Turno no encontrado"));
        turno.setEstado(nuevoEstado);
        return turnoRepo.save(turno);
    }

    // Mapeos

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
}
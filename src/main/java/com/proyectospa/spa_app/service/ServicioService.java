package com.proyectospa.spa_app.service;

import com.proyectospa.spa_app.dto.ServicioDTO;
import com.proyectospa.spa_app.model.Servicio;
import com.proyectospa.spa_app.model.Usuario;
import com.proyectospa.spa_app.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepo;

    public Servicio crearServicio(Servicio servicio) {
        return servicioRepo.save(servicio);
    }

    public List<Servicio> listarTodos() {
        return servicioRepo.findAll();
    }

    public List<Servicio> listarPorProfesional(Integer profesionalId) {
        return servicioRepo.findByProfesionalId(profesionalId);
    }


    public ServicioDTO toDTO(Servicio servicio) {
        ServicioDTO dto = new ServicioDTO();
        dto.setId(servicio.getId());
        dto.setNombre(servicio.getNombre());
        dto.setDescripcion(servicio.getDescripcion());
        dto.setPrecio(servicio.getPrecio());
        if(servicio.getProfesional() != null) {
            dto.setProfesionalId(servicio.getProfesional().getId());
        }
        return dto;
    }

    public Servicio toEntity(ServicioDTO dto, Usuario profesional) {
        Servicio servicio = new Servicio();
        servicio.setId(dto.getId());
        servicio.setNombre(dto.getNombre());
        servicio.setDescripcion(dto.getDescripcion());
        servicio.setPrecio(dto.getPrecio());
        servicio.setProfesional(profesional);
        return servicio;
    }

    public List<ServicioDTO> toDTOList(List<Servicio> servicios) {
        return servicios.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}

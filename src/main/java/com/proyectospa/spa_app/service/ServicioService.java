package com.proyectospa.spa_app.service;

import com.proyectospa.spa_app.model.Servicio;
import com.proyectospa.spa_app.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}

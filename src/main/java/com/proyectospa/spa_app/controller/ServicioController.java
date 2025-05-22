package com.proyectospa.spa_app.controller;

import com.proyectospa.spa_app.model.Servicio;
import com.proyectospa.spa_app.service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    @PostMapping("/crear")
    public ResponseEntity<Servicio> crearServicio(@RequestBody Servicio servicio) {
        Servicio nuevoServicio = servicioService.crearServicio(servicio);
        return ResponseEntity.ok(nuevoServicio);
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Servicio>> listarTodos() {
        List<Servicio> servicios = servicioService.listarTodos();
        return ResponseEntity.ok(servicios);
    }

    @GetMapping("/profesional/{id}")
    public ResponseEntity<List<Servicio>> listarPorProfesional(@PathVariable Integer id) {
        List<Servicio> servicios = servicioService.listarPorProfesional(id);
        return ResponseEntity.ok(servicios);
    }
}
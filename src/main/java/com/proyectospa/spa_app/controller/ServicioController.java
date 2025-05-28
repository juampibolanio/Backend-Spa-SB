package com.proyectospa.spa_app.controller;

import com.proyectospa.spa_app.dto.ServicioDTO;
import com.proyectospa.spa_app.model.Usuario;
import com.proyectospa.spa_app.service.ServicioService;
import com.proyectospa.spa_app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    @Autowired
    private UsuarioService usuarioService; 

    @PostMapping("/crear")
    public ResponseEntity<?> crearServicio(@RequestBody ServicioDTO dto) {
        try {
            var profesionalOpt = usuarioService.buscarPorId(dto.getProfesionalId());
            if (profesionalOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Profesional no encontrado");
            }
            Usuario profesional = profesionalOpt.get();
            var servicio = servicioService.toEntity(dto, profesional);
            ServicioDTO creado = servicioService.toDTO(servicioService.crearServicio(servicio));
            return ResponseEntity.ok(creado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/todos")
    public ResponseEntity<List<ServicioDTO>> listarTodos() {
        return ResponseEntity.ok(servicioService.toDTOList(servicioService.listarTodos()));
    }

    @GetMapping("/profesional/{id}")
    public ResponseEntity<List<ServicioDTO>> listarPorProfesional(@PathVariable Integer id) {
        return ResponseEntity.ok(servicioService.toDTOList(servicioService.listarPorProfesional(id)));
    }
}

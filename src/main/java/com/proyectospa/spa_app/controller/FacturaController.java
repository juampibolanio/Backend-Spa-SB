package com.proyectospa.spa_app.controller;

import com.proyectospa.spa_app.dto.FacturaDTO;
import com.proyectospa.spa_app.dto.FlujoCajaDTO;
import com.proyectospa.spa_app.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    // Listar todas las facturas
    @GetMapping
    public List<FacturaDTO> listarTodas() {
        return facturaService.listarTodas();
    }

    // Crear nueva factura
    @PostMapping
    public FacturaDTO crear(@RequestBody FacturaDTO dto) {
        try {
            return facturaService.guardar(dto);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar la factura");
        }
    }

    // Eliminar factura por ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        try {
            facturaService.eliminar(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar la factura");
        }
    }

    // Listar facturas por usuario
    @GetMapping("/usuario/{id}")
    public List<FacturaDTO> porUsuario(@PathVariable Integer id) {
        try {
            return facturaService.listarPorUsuario(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al consultar facturas por usuario");
        }
    }

    // Generar reporte por rango de fechas
    @GetMapping("/reporte")
    public List<FacturaDTO> reportePorFecha(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta
    ) {
        try {
            return facturaService.reportePorFecha(desde, hasta);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar reporte por fechas");
        }
    }

    @GetMapping("/flujo-caja")
public FlujoCajaDTO flujoCaja(
        @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
        @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta
) {
    try {
        return facturaService.generarFlujoCaja(desde, hasta);
    } catch (Exception e) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al generar flujo de caja");
    }
}
}

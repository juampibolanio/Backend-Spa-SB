package com.proyectospa.spa_app.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyectospa.spa_app.service.ReporteService;
import com.proyectospa.spa_app.service.TurnoService;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private TurnoService turnoService;

    @Autowired
    private ReporteService reporteService;

    
    @GetMapping("/pagos")
    public ResponseEntity<?> obtenerReportePagos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Integer profesionalId,
            @RequestParam(required = false) Integer servicioId) {

        if (desde.isAfter(hasta)) {
            return ResponseEntity.badRequest().body("La fecha 'desde' no puede ser posterior a la fecha 'hasta'");
        }

        Map<String, Object> reporte = turnoService.generarReportePagos(desde, hasta, profesionalId, servicioId);
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/pagos/profesional")
    public ResponseEntity<?> reportePorProfesional(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        if (desde.isAfter(hasta)) {
            return ResponseEntity.badRequest().body("La fecha 'desde' no puede ser posterior a la fecha 'hasta'");
        }

        List<Map<String, Object>> reporte = reporteService.reportePagosAgrupadoPorProfesional(desde, hasta);
        return ResponseEntity.ok(reporte);
    }

    
    @GetMapping("/pagos/servicio")
    public ResponseEntity<?> reportePorServicio(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        if (desde.isAfter(hasta)) {
            return ResponseEntity.badRequest().body("La fecha 'desde' no puede ser posterior a la fecha 'hasta'");
        }

        List<Map<String, Object>> reporte = reporteService.reportePagosAgrupadoPorServicio(desde, hasta);
        return ResponseEntity.ok(reporte);
    }
}

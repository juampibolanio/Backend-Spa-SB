package com.proyectospa.spa_app.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta,
            @RequestParam(required = false) Integer profesionalId,
            @RequestParam(required = false) Integer servicioId) {
        return ResponseEntity.ok(turnoService.generarReportePagos(desde, hasta, profesionalId, servicioId));
    }

    @GetMapping("/pagos/profesional")
    public ResponseEntity<List<Map<String, Object>>> reportePorProfesional(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        List<Map<String, Object>> reporte = reporteService.reportePagosAgrupadoPorProfesional(desde, hasta);
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/pagos/servicio")
    public ResponseEntity<List<Map<String, Object>>> reportePorServicio(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        List<Map<String, Object>> reporte = reporteService.reportePagosAgrupadoPorServicio(desde, hasta);
        return ResponseEntity.ok(reporte);
    }

}

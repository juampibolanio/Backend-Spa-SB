package com.proyectospa.spa_app.controller;

import com.proyectospa.spa_app.dto.FacturaDTO;
import com.proyectospa.spa_app.dto.FlujoCajaDTO;
import com.proyectospa.spa_app.dto.ReporteExistenciaDTO;
import com.proyectospa.spa_app.dto.VentaDetalladaDTO;
import com.proyectospa.spa_app.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/facturas")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @GetMapping("/clientes/{clienteId}/descuento-frecuente")
public ResponseEntity<Map<String, Object>> verificarDescuentoFrecuente(@PathVariable Integer clienteId) {
    try {
        System.out.println("🔍 Solicitando verificación de descuento para cliente: " + clienteId);
        Map<String, Object> respuesta = facturaService.verificarDescuentoClienteFrecuente(clienteId);
        System.out.println("✅ Respuesta descuento: " + respuesta);
        return ResponseEntity.ok(respuesta);
    } catch (IllegalArgumentException e) {
        System.err.println("❌ Error verificando descuento: " + e.getMessage());
        return ResponseEntity.notFound().build();
    } catch (Exception e) {
        System.err.println("❌ Error interno verificando descuento: " + e.getMessage());
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
            "Error al verificar descuento: " + e.getMessage());
    }
}

    // Listar todas las facturas
    @GetMapping
    public List<FacturaDTO> listarTodas() {
        return facturaService.listarTodas();
    }

    // Crear nueva factura
    @PostMapping
public FacturaDTO crear(@RequestBody FacturaDTO dto) {
    try {
        System.out.println("📥 Recibiendo factura para usuario: " + dto.getUsuarioId());
        FacturaDTO resultado = facturaService.guardar(dto);
        System.out.println("✅ Factura guardada exitosamente: " + resultado.getId());
        return resultado;
    } catch (Exception e) {
        System.err.println("❌ ERROR en FacturaController:");
        e.printStackTrace(); // ← ESTO NOS DARÁ EL ERROR REAL
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar la factura: " + e.getMessage());
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

@GetMapping("/reporte/existencias")
public List<ReporteExistenciaDTO> reporteExistencias(@RequestParam(required = false) Integer categoriaId,
                                                    @RequestParam(required = false) String nombreProducto) {
    return facturaService.generarReporteExistencias(categoriaId, nombreProducto);
}

@GetMapping("/reporte/ventas-detallado")
public List<VentaDetalladaDTO> reporteVentasDetallado(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
                                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
    return facturaService.generarReporteVentasDetallado(desde, hasta);
}
}

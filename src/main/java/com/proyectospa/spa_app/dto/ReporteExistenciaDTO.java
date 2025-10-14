package com.proyectospa.spa_app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReporteExistenciaDTO {
    private Integer productoId;
    private String productoNombre;
    private String categoriaNombre;
    private Integer stockActual;
    private BigDecimal precio;
    private LocalDateTime ultimaActualizacion;
}

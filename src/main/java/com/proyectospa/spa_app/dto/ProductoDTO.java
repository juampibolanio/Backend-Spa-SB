package com.proyectospa.spa_app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class ProductoDTO {
    private Integer id;
    private String nombre;
    private BigDecimal precio;
    private String descripcion;
    private String imagen;
    private String categoriaNombre;
    
    private boolean oferta;
    private Integer ventas;
    private LocalDate fechaLanzamiento;
    private BigDecimal descuento;
}

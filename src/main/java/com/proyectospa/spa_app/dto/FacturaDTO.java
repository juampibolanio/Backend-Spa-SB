package com.proyectospa.spa_app.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class FacturaDTO {
    private Integer id;
    private String metodoPago;
    private Integer usuarioId;        
    private String usuarioNombre;     
    private Integer productoId;       
    private String productoNombre;    
    private BigDecimal productoPrecio;
}

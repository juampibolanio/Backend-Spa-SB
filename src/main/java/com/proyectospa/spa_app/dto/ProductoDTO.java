package com.proyectospa.spa_app.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ProductoDTO {
    private Integer id;
    private String nombre;
    private BigDecimal precio;
    private String descripcion;
    private String imagen;
    private String categoriaNombre; 
}

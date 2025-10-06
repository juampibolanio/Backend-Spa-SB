package com.proyectospa.spa_app.dto;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class ProductoFacturaDTO {
    private Integer productoId;
    private String productoNombre;
    private BigDecimal productoPrecio;
    private Integer cantidad;
    private BigDecimal subtotal;
}
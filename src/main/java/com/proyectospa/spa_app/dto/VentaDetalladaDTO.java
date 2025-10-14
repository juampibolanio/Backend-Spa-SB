package com.proyectospa.spa_app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class VentaDetalladaDTO {
    private Integer facturaId;
    private LocalDateTime fechaVenta;
    private String productoNombre;
    private String categoriaNombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private String metodoPago;
}

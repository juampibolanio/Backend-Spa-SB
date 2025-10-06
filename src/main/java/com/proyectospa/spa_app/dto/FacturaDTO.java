package com.proyectospa.spa_app.dto;
import java.math.BigDecimal;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FacturaDTO {
    private Integer id;
    private String metodoPago;
    private Integer usuarioId;
    private String usuarioNombre;
    private List<ProductoFacturaDTO> productos;
    private BigDecimal descuento;
    private BigDecimal total;
    private LocalDateTime fecha;
}

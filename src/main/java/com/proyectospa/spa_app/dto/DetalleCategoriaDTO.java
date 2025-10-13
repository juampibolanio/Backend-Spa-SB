package com.proyectospa.spa_app.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DetalleCategoriaDTO {
    private String categoriaNombre;
    private List<ProductoFacturaDTO> productos;
    private BigDecimal subtotalCategoria;
}
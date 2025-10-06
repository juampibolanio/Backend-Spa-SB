package com.proyectospa.spa_app.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class FlujoCajaDTO {
    private List<DetalleCategoriaDTO> detallePorCategoria;
    private BigDecimal totalGeneral;
    private BigDecimal totalEfectivo;
    private BigDecimal totalDebito;
    private BigDecimal totalCredito;
}

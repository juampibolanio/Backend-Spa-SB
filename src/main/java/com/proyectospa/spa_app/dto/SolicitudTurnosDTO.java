package com.proyectospa.spa_app.dto;

import lombok.Data;
import java.util.List;

@Data
public class SolicitudTurnosDTO {
    private Integer clienteId;
    private Integer profesionalId;
    private List<TurnoPorDiaDTO> turnosPorDia;
    private boolean pagoWeb;
    private String metodoPago;
}

package com.proyectospa.spa_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TurnoProfesionalDTO {
    private Integer id;
    private String clienteNombre;
    private String clienteApellido;
    public Integer clienteId;
    private String servicioNombre;
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private String estado;
    private boolean pagado;
    private boolean pagoWeb;
    private String metodoPago;
    private double monto;
    private String detalle;
    
}
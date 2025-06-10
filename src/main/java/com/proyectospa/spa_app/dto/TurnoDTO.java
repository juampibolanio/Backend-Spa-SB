package com.proyectospa.spa_app.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TurnoDTO {
    private Integer id;
    private Integer clienteId;
    private Integer profesionalId;
    private Integer servicioId;
    private String estado;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private boolean pagado;
    private boolean pagoWeb;
    private String metodoPago; 
    private double monto; 
    private String detalle;
}
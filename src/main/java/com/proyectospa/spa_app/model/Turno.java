package com.proyectospa.spa_app.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Usuario cliente;

    @ManyToOne
    @JoinColumn(name = "profesional_id")
    private Usuario profesional;

    @ManyToOne
    @JoinColumn(name = "servicio_id")
    private Servicio servicio;

    @Enumerated(EnumType.STRING)
    private EstadoTurno estado;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;

    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    private boolean pagado;
    private boolean pagoWeb;
    private double monto;
    @Column(length = 500)
    private String detalle; 

}
package com.proyectospa.spa_app.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private String descripcion;
    private double precio;

    private LocalDateTime fecha; 

    @ManyToOne
    @JoinColumn(name = "profesional_id")
    private Usuario profesional;
}
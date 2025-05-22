package com.proyectospa.spa_app.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ProfesionalDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private String especialidad;
    private String telefono;
    private String descripcion;
    private String fotoUrl;
}
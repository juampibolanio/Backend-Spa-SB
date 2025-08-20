package com.proyectospa.spa_app.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
}

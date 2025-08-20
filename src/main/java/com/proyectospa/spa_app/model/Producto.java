package com.proyectospa.spa_app.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private BigDecimal precio;
    private String descripcion;
    private String imagen;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

}

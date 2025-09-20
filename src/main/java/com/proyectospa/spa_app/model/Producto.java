package com.proyectospa.spa_app.model;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    private boolean oferta;
    private Integer ventas;
    private LocalDate fechaLanzamiento;
    private BigDecimal descuento;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

}

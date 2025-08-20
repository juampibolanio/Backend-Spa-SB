package com.proyectospa.spa_app.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;
}

package com.proyectospa.spa_app.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;


@Data
@Entity
public class FacturaProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "factura_id")
    private Factura factura;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private Integer cantidad;
    private BigDecimal subtotal;
}
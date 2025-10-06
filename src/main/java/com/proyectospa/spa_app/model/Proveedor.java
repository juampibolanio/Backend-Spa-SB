package com.proyectospa.spa_app.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;

    private String contacto;

    private String direccion;

    @OneToMany(mappedBy = "proveedor")
    private List<Producto> productos;
}

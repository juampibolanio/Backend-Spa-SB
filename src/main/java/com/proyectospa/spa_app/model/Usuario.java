package com.proyectospa.spa_app.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private String apellido;
    private String dni;
    private String email;

    private String password;
    private String telefono;
    
    @Enumerated(EnumType.STRING)
    private Rol rol;

    private boolean activo = true;
}
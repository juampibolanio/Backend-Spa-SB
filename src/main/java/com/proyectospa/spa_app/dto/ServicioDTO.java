package com.proyectospa.spa_app.dto;

import lombok.Data;

@Data
public class ServicioDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private double precio;
    private Integer profesionalId;  
}

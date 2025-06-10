package com.proyectospa.spa_app.dto;

import lombok.Data;

@Data
public class ClienteResumenDTO {
    private Integer id;
    private String nombre;
    private String apellido;
    private String email;

    public ClienteResumenDTO(Integer id, String nombre, String apellido, String email) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

}

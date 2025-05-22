package com.proyectospa.spa_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UsuarioDTO {
    private Integer id;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String rol;
    private boolean activo;
}

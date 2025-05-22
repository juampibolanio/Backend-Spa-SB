package com.proyectospa.spa_app.dto;

import com.proyectospa.spa_app.model.Rol;
import lombok.Data;

@Data
public class RegistroUsuarioDTO {
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String password;
    private Rol rol;
}

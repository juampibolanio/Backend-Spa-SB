package com.proyectospa.spa_app.dto;

import com.proyectospa.spa_app.model.Usuario;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UsuarioDTO {
    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.dni = usuario.getDni();
        this.email = usuario.getEmail();
        this.rol = usuario.getRol().name(); 
        this.activo = usuario.isActivo();
    }

    private Integer id;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String rol;
    private boolean activo;
}

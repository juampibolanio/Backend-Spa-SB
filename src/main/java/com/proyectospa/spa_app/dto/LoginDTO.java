package com.proyectospa.spa_app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginDTO {
    private String email;
    private String password;
}

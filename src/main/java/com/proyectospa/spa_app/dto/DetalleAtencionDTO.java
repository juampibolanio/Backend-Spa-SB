package com.proyectospa.spa_app.dto;

import lombok.Data;

@Data
public class DetalleAtencionDTO {
    private String detalle;

    public DetalleAtencionDTO() {}

    public DetalleAtencionDTO(String detalle) {
        this.detalle = detalle;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
}
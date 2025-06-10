package com.proyectospa.spa_app.dto;

import lombok.Data;

@Data
public class TurnoHistorialDTO {
    private Integer id;
    private String servicio;
    private String fecha;
    private String horaInicio;
    private String horaFin;
    private String estado;
    private boolean pagado;
    private String metodoPago;
    private Double monto;
    private String detalleAtencion;

    public TurnoHistorialDTO(Integer id, String servicio, String fecha, String horaInicio, String horaFin,
            String estado, boolean pagado, String metodoPago, Double monto, String detalleAtencion) {
        this.id = id;
        this.servicio = servicio;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
        this.pagado = pagado;
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.detalleAtencion = detalleAtencion;
    }

}
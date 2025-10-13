package com.proyectospa.spa_app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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

    @OneToMany(mappedBy = "cliente")
    private List<Turno> turnos;

    public List<Servicio> getServiciosRecientes() {
        if (turnos == null) return List.of();
        return turnos.stream()
                .sorted(Comparator.comparing(Turno::getFecha).reversed())
                .map(Turno::getServicio)
                .distinct()
                .limit(5)
                .collect(Collectors.toList());
    }
}

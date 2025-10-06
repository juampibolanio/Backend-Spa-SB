package com.proyectospa.spa_app.repository;

import com.proyectospa.spa_app.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FacturaRepository extends JpaRepository<Factura, Integer> {
    List<Factura> findByUsuario_Id(Integer usuarioId);

    // Para filtrar por producto dentro de la factura
    @Query("SELECT f FROM Factura f JOIN f.productos fp WHERE fp.producto.id = :productoId")
    List<Factura> findByProductoId(@Param("productoId") Integer productoId);

    List<Factura> findByFechaBetween(LocalDateTime desde, LocalDateTime hasta);
}

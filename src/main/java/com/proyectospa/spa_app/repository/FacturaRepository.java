package com.proyectospa.spa_app.repository;

import com.proyectospa.spa_app.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FacturaRepository extends JpaRepository<Factura, Integer> {
    //listar facturas por usuario
    List<Factura> findByUsuario_Id(Integer usuarioId);

    //listar facturas por producto
    List<Factura> findByProducto_Id(Integer productoId);
}

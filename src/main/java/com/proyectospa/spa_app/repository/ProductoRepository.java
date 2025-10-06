package com.proyectospa.spa_app.repository;

import com.proyectospa.spa_app.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    // Filtrar por categoría
    List<Producto> findByCategoria_Nombre(String categoriaNombre);

    // Filtrar solo ofertas
    List<Producto> findByOfertaTrue();

    // Filtrar por rango de precio
    List<Producto> findByPrecioBetween(BigDecimal min, BigDecimal max);

    // Ordenar por precio ascendente
    List<Producto> findAllByOrderByPrecioAsc();

    // Ordenar por precio descendente
    List<Producto> findAllByOrderByPrecioDesc();

    // Ordenar por más vendidos
    List<Producto> findAllByOrderByVentasDesc();

    // Ordenar por fecha de lanzamiento (del más reciente al más antiguo)
    List<Producto> findAllByOrderByFechaLanzamientoDesc();

    // Ordenar por fecha de lanzamiento (del más antiguo al más reciente)
    List<Producto> findAllByOrderByFechaLanzamientoAsc();

    // Filtrar productos con descuento
    List<Producto> findByDescuentoGreaterThan(BigDecimal valorMinimo);

    //combinación: categoría + oferta + rango de precio
    @Query("SELECT p FROM Producto p WHERE " +
            "(:categoria IS NULL OR p.categoria.nombre = :categoria) AND " +
            "(:oferta IS NULL OR p.oferta = :oferta) AND " +
            "(:minPrecio IS NULL OR :maxPrecio IS NULL OR p.precio BETWEEN :minPrecio AND :maxPrecio)")
    List<Producto> filtrarProductos(
            @Param("categoria") String categoria,
            @Param("oferta") Boolean oferta,
            @Param("minPrecio") BigDecimal minPrecio,
            @Param("maxPrecio") BigDecimal maxPrecio
    );
}


package com.proyectospa.spa_app.repository;

import com.proyectospa.spa_app.model.Categoria;
import com.proyectospa.spa_app.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByCategoria_Nombre(String categoriaNombre);

    List<Producto> findByOfertaTrue();

    List<Producto> findByPrecioBetween(BigDecimal min, BigDecimal max);

    List<Producto> findAllByOrderByPrecioAsc();
    List<Producto> findAllByOrderByPrecioDesc();
    List<Producto> findAllByOrderByVentasDesc();
    List<Producto> findAllByOrderByFechaLanzamientoDesc();
    List<Producto> findAllByOrderByFechaLanzamientoAsc();

    List<Producto> findByDescuentoGreaterThan(BigDecimal valorMinimo);

    @Query("SELECT p FROM Producto p WHERE " +
            "(:categoria IS NULL OR p.categoria.nombre = :categoria) AND " +
            "(:oferta IS NULL OR p.oferta = :oferta) AND " +
            "((:minPrecio IS NULL AND :maxPrecio IS NULL) OR p.precio BETWEEN COALESCE(:minPrecio, p.precio) AND COALESCE(:maxPrecio, p.precio))")
    List<Producto> filtrarProductos(
            @Param("categoria") String categoria,
            @Param("oferta") Boolean oferta,
            @Param("minPrecio") BigDecimal minPrecio,
            @Param("maxPrecio") BigDecimal maxPrecio
    );

    @Query("SELECT p FROM Producto p " +
       "WHERE (:categoriaId IS NULL OR p.categoria.id = :categoriaId) " +
       "AND (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))")
List<Producto> buscarPorFiltros(@Param("categoriaId") Integer categoriaId,
                                @Param("nombre") String nombre);

List<Producto> findByStockLessThanEqual(Integer stockMax);

@Query("SELECT p FROM Producto p WHERE " +
           "(:categoriaId IS NULL OR p.categoria.id = :categoriaId) AND " +
           "(:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:stockMin IS NULL OR p.stock >= :stockMin) AND " +
           "(:stockMax IS NULL OR p.stock <= :stockMax)")
    List<Producto> buscarConFiltrosStock(@Param("categoriaId") Integer categoriaId,
                                        @Param("nombre") String nombre,
                                        @Param("stockMin") Integer stockMin,
                                        @Param("stockMax") Integer stockMax);



                                
}



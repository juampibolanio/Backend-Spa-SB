package com.proyectospa.spa_app.service;

import com.proyectospa.spa_app.dto.ProductoDTO;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IProductoService {
    List<ProductoDTO> listarTodos();
    Optional<ProductoDTO> obtenerPorId(Integer id);
    ProductoDTO guardar(ProductoDTO productoDTO, MultipartFile imagen);
    void eliminar(Integer id);

    // Filtros 
    List<ProductoDTO> listarPorCategoria(String categoriaNombre);
    List<ProductoDTO> listarOfertas();
    List<ProductoDTO> listarPorRangoDePrecio(BigDecimal min, BigDecimal max);
    List<ProductoDTO> listarPorDescuentoMinimo(BigDecimal descuentoMin);
    List<ProductoDTO> listarPorFiltros(Integer categoriaId, String nombre);
}

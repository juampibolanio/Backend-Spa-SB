package com.proyectospa.spa_app.service;

import com.proyectospa.spa_app.dto.ProductoDTO;
import com.proyectospa.spa_app.model.Producto;
import com.proyectospa.spa_app.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
public class ProductoService implements IProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    private ProductoDTO convertirADTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setDescripcion(producto.getDescripcion());
        dto.setImagen(producto.getImagen());
        dto.setOferta(producto.isOferta());
        dto.setVentas(producto.getVentas());
        dto.setFechaLanzamiento(producto.getFechaLanzamiento());
        dto.setDescuento(producto.getDescuento());
        if (producto.getCategoria() != null) {
            dto.setCategoriaNombre(producto.getCategoria().getNombre());
        }
        return dto;
    }

    private Producto convertirAEntidad(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setId(dto.getId());
        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setDescripcion(dto.getDescripcion());
        producto.setImagen(dto.getImagen());
        producto.setOferta(dto.isOferta());
        producto.setVentas(dto.getVentas());
        producto.setFechaLanzamiento(dto.getFechaLanzamiento());
        producto.setDescuento(dto.getDescuento());
        return producto;
    }

    @Override
    public List<ProductoDTO> listarTodos() {
        return productoRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductoDTO> obtenerPorId(Integer id) {
        return productoRepository.findById(id).map(this::convertirADTO);
    }

    @Override
    public ProductoDTO guardar(ProductoDTO productoDTO) {
        Producto producto = convertirAEntidad(productoDTO);
        Producto guardado = productoRepository.save(producto);
        return convertirADTO(guardado);
    }

    @Override
    public void eliminar(Integer id) {
        productoRepository.deleteById(id);
    }

    @Override
    public List<ProductoDTO> listarPorCategoria(String categoriaNombre) {
        return productoRepository.findByCategoria_Nombre(categoriaNombre)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductoDTO> listarOfertas() {
        return productoRepository.findByOfertaTrue()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductoDTO> listarPorRangoDePrecio(BigDecimal min, BigDecimal max) {
        return productoRepository.findByPrecioBetween(min, max)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductoDTO> listarPorDescuentoMinimo(BigDecimal descuentoMin) {
        return productoRepository.findByDescuentoGreaterThan(descuentoMin)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<ProductoDTO> listarPorFiltros(Integer categoriaId, String nombre) {
    return productoRepository.buscarPorFiltros(categoriaId, nombre)
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
}
}

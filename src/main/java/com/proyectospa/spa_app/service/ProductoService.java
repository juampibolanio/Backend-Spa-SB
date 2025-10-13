package com.proyectospa.spa_app.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.proyectospa.spa_app.dto.ProductoDTO;
import com.proyectospa.spa_app.model.Producto;
import com.proyectospa.spa_app.repository.CategoriaRepository;
import com.proyectospa.spa_app.repository.ProductoRepository;
import com.proyectospa.spa_app.repository.ProveedorRepository;
import com.proyectospa.spa_app.util.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoService implements IProductoService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private Cloudinary cloudinary;

    private ProductoDTO convertirADTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setDescripcion(producto.getDescripcion());
        dto.setImagen(producto.getImagen());
        dto.setCategoria_id(producto.getCategoria() != null ? producto.getCategoria().getId() : null);
        dto.setProveedor_id(producto.getProveedor() != null ? producto.getProveedor().getId() : null);
        dto.setStock(producto.getStock() != 0 ? producto.getStock() : 0);
        dto.setOferta(producto.isOferta());
        dto.setVentas(producto.getVentas());
        dto.setFechaLanzamiento(producto.getFechaLanzamiento());
        dto.setDescuento(producto.getDescuento());
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
        producto.setStock(dto.getStock());

        if (dto.getCategoria_id() != null) {
            producto.setCategoria(categoriaRepository.findById(dto.getCategoria_id()).orElse(null));
        } else {
            producto.setCategoria(null);
        }

        if (dto.getProveedor_id() != null) {
            producto.setProveedor(proveedorRepository.findById(dto.getProveedor_id()).orElse(null));
        } else {
            producto.setProveedor(null);
        }

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
    public ProductoDTO guardar(ProductoDTO productoDTO, MultipartFile imagen) {
        Producto producto = convertirAEntidad(productoDTO);

        try {
            if (imagen != null && !imagen.isEmpty()) {
                Map uploadResult = cloudinary.uploader().upload(imagen.getBytes(),
                        ObjectUtils.asMap("folder", "productos_spa"));
                String url = (String) uploadResult.get("secure_url");
                producto.setImagen(url);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error uploading image", e);
        }

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

    @Override
    public List<ProductoDTO> listarPorFiltros(Integer categoriaId, String nombre) {
        return productoRepository.buscarPorFiltros(categoriaId, nombre)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
}

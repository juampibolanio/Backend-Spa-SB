package com.proyectospa.spa_app.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.proyectospa.spa_app.dto.ProductoDTO;
import com.proyectospa.spa_app.model.Producto;
import com.proyectospa.spa_app.repository.CategoriaRepository;
import com.proyectospa.spa_app.repository.ProductoRepository;
import com.proyectospa.spa_app.repository.ProveedorRepository;
import com.proyectospa.spa_app.util.CloudinaryService;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Transactional
public ProductoDTO actualizarSoloStock(Integer id, Integer nuevoStock) {
    Producto producto = productoRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
    
    // ✅ PRESERVAR todos los demás campos, solo cambiar stock
    producto.setStock(nuevoStock);
    
    Producto productoActualizado = productoRepository.save(producto);
    return convertirADTO(productoActualizado);
}

    private ProductoDTO convertirADTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setDescripcion(producto.getDescripcion());
        dto.setImagen(producto.getImagen());
        dto.setCategoria_id(producto.getCategoria() != null ? producto.getCategoria().getId() : null);
        dto.setProveedor_id(producto.getProveedor() != null ? producto.getProveedor().getId() : null);
        dto.setStock(producto.getStock() != null ? producto.getStock() : 0); // ← Asegurar que stock se mapee
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
            // ✅ SUBIR NUEVA IMAGEN si se proporciona
            Map uploadResult = cloudinary.uploader().upload(imagen.getBytes(),
                    ObjectUtils.asMap("folder", "productos_spa"));
            String url = (String) uploadResult.get("secure_url");
            producto.setImagen(url);
        } else if (productoDTO.getId() != null) {
            // ✅ PRESERVAR IMAGEN EXISTENTE cuando se edita sin nueva imagen
            Producto productoExistente = productoRepository.findById(productoDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            producto.setImagen(productoExistente.getImagen());
        }
        // ✅ Si es nuevo producto y no hay imagen, queda null (como antes)
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

    // ✅ NUEVO: Obtener productos con stock bajo
    public List<ProductoDTO> obtenerProductosStockBajo(Integer limite) {
        return productoRepository.findByStockLessThanEqual(limite)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

// ✅ NUEVO: Actualizar stock de producto
    @PutMapping("/{id}/stock")
public ResponseEntity<ProductoDTO> actualizarStock(@PathVariable Integer id, @RequestParam Integer nuevoStock) {
    Producto producto = productoRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
    
    // ✅ SOLO actualizar el stock, no tocar otros campos
    producto.setStock(nuevoStock);
    Producto productoActualizado = productoRepository.save(producto);
    
    return ResponseEntity.ok(convertirADTO(productoActualizado));
}

// ✅ NUEVO: Consultar stock con filtros
    public List<ProductoDTO> consultarStock(Integer categoriaId, String nombre, Integer stockMin, Integer stockMax) {
        return productoRepository.buscarConFiltrosStock(categoriaId, nombre, stockMin, stockMax)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }
}

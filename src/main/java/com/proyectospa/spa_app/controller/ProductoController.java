package com.proyectospa.spa_app.controller;

import com.proyectospa.spa_app.model.Producto;
import com.proyectospa.spa_app.dto.ProductoDTO;
import com.proyectospa.spa_app.repository.ProductoRepository;
import com.proyectospa.spa_app.service.ProductoService;
import com.proyectospa.spa_app.repository.CategoriaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoService productoService;

    //agregar producto
    @PostMapping
    public ResponseEntity<ProductoDTO> crearProducto(
            @RequestPart("producto") ProductoDTO productoDTO,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen
    ) throws IOException {

        if (productoDTO.getCategoria_id() != null) {
            categoriaRepository.findById(productoDTO.getCategoria_id())
                    .ifPresent(c -> productoDTO.setCategoria_id(c.getId()));
        }

        ProductoDTO nuevoProducto = productoService.guardar(productoDTO, imagen);
        return ResponseEntity.ok(nuevoProducto);
    }

    //obtener todos los productos
    @GetMapping
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    //obtener producto por id
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable Integer id) {
        return productoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //modificar producto
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizarProducto(
            @PathVariable Integer id,
            @RequestPart("producto") ProductoDTO productoDTO,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen
    ) throws IOException {

        if (productoDTO.getCategoria_id() != null) {
            categoriaRepository.findById(productoDTO.getCategoria_id())
                    .ifPresent(c -> productoDTO.setCategoria_id(c.getId()));
        }

        productoDTO.setId(id);
        ProductoDTO actualizado = productoService.guardar(productoDTO, imagen);
        return ResponseEntity.ok(actualizado);
    }

    //eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Integer id) {
        return productoRepository.findById(id)
                .map(producto -> {
                    productoRepository.delete(producto);
                    return ResponseEntity.ok().<Void>build();
                }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/consulta")
    public List<ProductoDTO> consultarExistencia(
        @RequestParam(required = false) Integer categoriaId,
        @RequestParam(required = false) String nombre
    ) {
    return productoService.listarPorFiltros(categoriaId, nombre);
    }

    @GetMapping("/stock/bajo")
public List<ProductoDTO> productosStockBajo(@RequestParam(defaultValue = "5") Integer limite) {
    return productoService.obtenerProductosStockBajo(limite);
}

@PutMapping("/{id}/stock")
public ResponseEntity<ProductoDTO> actualizarStock(@PathVariable Integer id, @RequestParam Integer nuevoStock) {
    try {
        ProductoDTO productoActualizado = productoService.actualizarSoloStock(id, nuevoStock);
        return ResponseEntity.ok(productoActualizado);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.notFound().build();
    } catch (Exception e) {
        return ResponseEntity.internalServerError().build();
    }
}

@GetMapping("/stock/consulta")
public List<ProductoDTO> consultarStock(@RequestParam(required = false) Integer categoriaId,
                                       @RequestParam(required = false) String nombre,
                                       @RequestParam(required = false) Integer stockMin,
                                       @RequestParam(required = false) Integer stockMax) {
    return productoService.consultarStock(categoriaId, nombre, stockMin, stockMax);
}
}

package com.proyectospa.spa_app.controller;

import com.proyectospa.spa_app.model.Producto;
import com.proyectospa.spa_app.dto.ProductoDTO;
import com.proyectospa.spa_app.model.Categoria;
import com.proyectospa.spa_app.repository.ProductoRepository;
import com.proyectospa.spa_app.service.ProductoService;
import com.proyectospa.spa_app.repository.CategoriaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        if (producto.getCategoria() != null) {
            Optional<Categoria> cat = categoriaRepository.findById(producto.getCategoria().getId());
            cat.ifPresent(producto::setCategoria);
        }
        Producto nuevoProducto = productoRepository.save(producto);
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
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Integer id, @RequestBody Producto productoActualizado) {
        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setNombre(productoActualizado.getNombre());
                    producto.setPrecio(productoActualizado.getPrecio());
                    producto.setDescripcion(productoActualizado.getDescripcion());
                    producto.setImagen(productoActualizado.getImagen());
                    producto.setOferta(productoActualizado.isOferta());
                    producto.setVentas(productoActualizado.getVentas());
                    producto.setFechaLanzamiento(productoActualizado.getFechaLanzamiento());
                    producto.setDescuento(productoActualizado.getDescuento());

                    if (productoActualizado.getCategoria() != null) {
                        categoriaRepository.findById(productoActualizado.getCategoria().getId())
                                .ifPresent(producto::setCategoria);
                    }

                    Producto actualizado = productoRepository.save(producto);
                    return ResponseEntity.ok(actualizado);
                }).orElse(ResponseEntity.notFound().build());
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
}

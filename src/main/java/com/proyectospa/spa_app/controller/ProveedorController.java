package com.proyectospa.spa_app.controller;

import com.proyectospa.spa_app.dto.ProveedorDTO;
import com.proyectospa.spa_app.model.Proveedor;
import com.proyectospa.spa_app.service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @PostMapping("/registrar")
    public ResponseEntity<ProveedorDTO> registrarProveedor(@RequestBody ProveedorDTO dto) {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(dto.getNombre());
        proveedor.setContacto(dto.getContacto());
        proveedor.setDireccion(dto.getDireccion());

        Proveedor guardado = proveedorService.guardarProveedor(proveedor);

        ProveedorDTO respuesta = new ProveedorDTO(
            guardado.getId(),
            guardado.getNombre(),
            guardado.getContacto(),
            guardado.getDireccion()
        );

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorDTO> obtenerProveedor(@PathVariable Integer id) {
        Optional<Proveedor> proveedorOpt = proveedorService.buscarPorId(id);
        if (proveedorOpt.isPresent()) {
            Proveedor p = proveedorOpt.get();
            ProveedorDTO dto = new ProveedorDTO(p.getId(), p.getNombre(), p.getContacto(), p.getDireccion());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/todos")
    public ResponseEntity<List<ProveedorDTO>> listarTodos() {
        List<ProveedorDTO> lista = proveedorService.listarTodos().stream()
            .map(p -> new ProveedorDTO(p.getId(), p.getNombre(), p.getContacto(), p.getDireccion()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/{id}/actualizar")
    public ResponseEntity<ProveedorDTO> actualizarProveedor(@PathVariable Integer id, @RequestBody ProveedorDTO dto) {
        Optional<Proveedor> proveedorOpt = proveedorService.buscarPorId(id);
        if (proveedorOpt.isPresent()) {
            Proveedor proveedor = proveedorOpt.get();
            proveedor.setNombre(dto.getNombre());
            proveedor.setContacto(dto.getContacto());
            proveedor.setDireccion(dto.getDireccion());

            Proveedor actualizado = proveedorService.guardarProveedor(proveedor);
            ProveedorDTO respuesta = new ProveedorDTO(
                actualizado.getId(),
                actualizado.getNombre(),
                actualizado.getContacto(),
                actualizado.getDireccion()
            );

            return ResponseEntity.ok(respuesta);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable Integer id) {
        Optional<Proveedor> proveedorOpt = proveedorService.buscarPorId(id);
        if (proveedorOpt.isPresent()) {
            proveedorService.eliminarProveedor(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

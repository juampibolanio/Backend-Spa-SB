package com.proyectospa.spa_app.service;

import com.proyectospa.spa_app.dto.FacturaDTO;
import com.proyectospa.spa_app.model.Factura;
import com.proyectospa.spa_app.model.Producto;
import com.proyectospa.spa_app.model.Usuario;
import com.proyectospa.spa_app.repository.FacturaRepository;
import com.proyectospa.spa_app.repository.ProductoRepository;
import com.proyectospa.spa_app.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // Listar todas las facturas
    public List<FacturaDTO> listarTodas() {
        return facturaRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Guardar nueva factura
    public FacturaDTO guardar(FacturaDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        Factura factura = new Factura();
        factura.setMetodoPago(Enum.valueOf(Factura.MetodoPago.class, dto.getMetodoPago()));
        factura.setUsuario(usuario);
        factura.setProducto(producto);

        Factura guardada = facturaRepository.save(factura);
        return convertirADTO(guardada);
    }

    // Eliminar factura
    public void eliminar(Integer id) {
        if (!facturaRepository.existsById(id)) {
            throw new IllegalArgumentException("Factura no encontrada con id: " + id);
        }
        facturaRepository.deleteById(id);
    }

    // Convertir Entity -> DTO
    private FacturaDTO convertirADTO(Factura factura) {
        FacturaDTO dto = new FacturaDTO();
        dto.setId(factura.getId());
        dto.setMetodoPago(factura.getMetodoPago().name());
        dto.setUsuarioId(factura.getUsuario().getId());
        dto.setUsuarioNombre(factura.getUsuario().getNombre());
        dto.setProductoId(factura.getProducto().getId());
        dto.setProductoNombre(factura.getProducto().getNombre());
        dto.setProductoPrecio(factura.getProducto().getPrecio());
        return dto;
    }
}

package com.proyectospa.spa_app.service;

import com.proyectospa.spa_app.dto.DetalleCategoriaDTO;
import com.proyectospa.spa_app.dto.FacturaDTO;
import com.proyectospa.spa_app.dto.FlujoCajaDTO;
import com.proyectospa.spa_app.dto.ProductoFacturaDTO;
import com.proyectospa.spa_app.model.*;
import com.proyectospa.spa_app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private FacturaProductoRepository facturaProductoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // Listar todas las facturas
    public List<FacturaDTO> listarTodas() {
        return facturaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Guardar nueva factura
    public FacturaDTO guardar(FacturaDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
                

        Factura factura = new Factura();
        factura.setUsuario(usuario);
        factura.setMetodoPago(Enum.valueOf(MetodoPago.class, dto.getMetodoPago()));
        factura.setFecha(LocalDateTime.now());

        // Guardar factura primero para asociar productos
        Factura guardada = facturaRepository.save(factura);

        BigDecimal total = BigDecimal.ZERO;

        for (ProductoFacturaDTO pfDTO : dto.getProductos()) {
            Producto producto = productoRepository.findById(pfDTO.getProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            FacturaProducto fp = new FacturaProducto();
            fp.setFactura(guardada);
            fp.setProducto(producto);
            fp.setCantidad(pfDTO.getCantidad() != null ? pfDTO.getCantidad() : 1);
            fp.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(fp.getCantidad())));

            total = total.add(fp.getSubtotal());

            facturaProductoRepository.save(fp);
        }

        // Aplicar descuentos
        BigDecimal descuento = BigDecimal.ZERO;

        if (factura.getMetodoPago() == MetodoPago.EFECTIVO) {
            descuento = total.multiply(BigDecimal.valueOf(0.10)); // 10% efectivo
        } else if (esClienteFrecuente(usuario)) {
            descuento = total.multiply(BigDecimal.valueOf(0.15)); // 15% cliente frecuente
        }

        factura.setDescuento(descuento);
        factura.setTotal(total.subtract(descuento));

        facturaRepository.save(factura);

        return convertirADTO(factura);
    }

    // Eliminar factura
    public void eliminar(Integer id) {
        if (!facturaRepository.existsById(id)) {
            throw new IllegalArgumentException("Factura no encontrada con id: " + id);
        }
        facturaRepository.deleteById(id);
    }

    // Consultas por usuario
    public List<FacturaDTO> listarPorUsuario(Integer usuarioId) {
        return facturaRepository.findByUsuario_Id(usuarioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Consultas por rango de fechas
    public List<FacturaDTO> reportePorFecha(LocalDateTime desde, LocalDateTime hasta) {
        return facturaRepository.findByFechaBetween(desde, hasta).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    // Lógica de cliente frecuente: mínimo 3 servicios en los últimos 30 días
    private boolean esClienteFrecuente(Usuario usuario) {
        LocalDateTime desde = LocalDateTime.now().minusDays(30);
        long serviciosRecientes = usuario.getServiciosRecientes().stream()
                .filter(s -> s.getFecha().isAfter(desde))
                .count();
        return serviciosRecientes >= 3;
    }

    // Conversión Entity DTO
    private FacturaDTO convertirADTO(Factura factura) {
        FacturaDTO dto = new FacturaDTO();
        dto.setId(factura.getId());
        dto.setMetodoPago(factura.getMetodoPago().name());
        dto.setUsuarioId(factura.getUsuario().getId());
        dto.setUsuarioNombre(factura.getUsuario().getNombre());
        dto.setDescuento(factura.getDescuento());
        dto.setTotal(factura.getTotal());
        dto.setFecha(factura.getFecha());

        List<ProductoFacturaDTO> productosDTO = factura.getProductos().stream()
                .map(fp -> {
                    ProductoFacturaDTO pfDTO = new ProductoFacturaDTO();
                    pfDTO.setProductoId(fp.getProducto().getId());
                    pfDTO.setProductoNombre(fp.getProducto().getNombre());
                    pfDTO.setProductoPrecio(fp.getProducto().getPrecio());
                    pfDTO.setCantidad(fp.getCantidad());
                    pfDTO.setSubtotal(fp.getSubtotal());
                    return pfDTO;
                })
                .collect(Collectors.toList());

        dto.setProductos(productosDTO);
        return dto;
    }

    public FlujoCajaDTO generarFlujoCaja(LocalDateTime desde, LocalDateTime hasta) {
    List<Factura> facturas = facturaRepository.findByFechaBetween(desde, hasta);

    BigDecimal totalGeneral = BigDecimal.ZERO;
    BigDecimal totalEfectivo = BigDecimal.ZERO;
    BigDecimal totalDebito = BigDecimal.ZERO;
    BigDecimal totalCredito = BigDecimal.ZERO;

    // Mapa para agrupar productos por categoría
    Map<String, List<ProductoFacturaDTO>> productosPorCategoria = new HashMap<>();

    for (Factura factura : facturas) {
        BigDecimal totalFactura = factura.getTotal() != null ? factura.getTotal() : BigDecimal.ZERO;
        totalGeneral = totalGeneral.add(totalFactura);

        switch (factura.getMetodoPago()) {
            case EFECTIVO -> totalEfectivo = totalEfectivo.add(totalFactura);
            case TARJETA_DEBITO -> totalDebito = totalDebito.add(totalFactura);
            case TARJETA_CREDITO -> totalCredito = totalCredito.add(totalFactura);
            default -> {}
        }

        for (FacturaProducto fp : factura.getProductos()) {
            Producto prod = fp.getProducto();
            String categoria = prod.getCategoria() != null ? prod.getCategoria().getNombre() : "Sin categoría";

            ProductoFacturaDTO pfDTO = new ProductoFacturaDTO();
            pfDTO.setProductoId(prod.getId());
            pfDTO.setProductoNombre(prod.getNombre());
            pfDTO.setProductoPrecio(prod.getPrecio());
            pfDTO.setCantidad(fp.getCantidad());
            pfDTO.setSubtotal(fp.getSubtotal());

            productosPorCategoria.computeIfAbsent(categoria, k -> new ArrayList<>()).add(pfDTO);
        }
    }

    // Se crea detalle por categoría
    List<DetalleCategoriaDTO> detallePorCategoria = productosPorCategoria.entrySet().stream()
        .map(entry -> {
            DetalleCategoriaDTO detCat = new DetalleCategoriaDTO();
            detCat.setCategoriaNombre(entry.getKey());
            detCat.setProductos(entry.getValue());
            BigDecimal subtotal = entry.getValue().stream()
                                     .map(ProductoFacturaDTO::getSubtotal)
                                     .reduce(BigDecimal.ZERO, BigDecimal::add);
            detCat.setSubtotalCategoria(subtotal);
            return detCat;
        })
        .collect(Collectors.toList());

    FlujoCajaDTO flujoDTO = new FlujoCajaDTO();
    flujoDTO.setDetallePorCategoria(detallePorCategoria);
    flujoDTO.setTotalGeneral(totalGeneral);
    flujoDTO.setTotalEfectivo(totalEfectivo);
    flujoDTO.setTotalDebito(totalDebito);
    flujoDTO.setTotalCredito(totalCredito);

    return flujoDTO;
}
}

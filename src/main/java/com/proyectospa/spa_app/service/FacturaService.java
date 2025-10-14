package com.proyectospa.spa_app.service;

import com.proyectospa.spa_app.dto.DetalleCategoriaDTO;
import com.proyectospa.spa_app.dto.FacturaDTO;
import com.proyectospa.spa_app.dto.FlujoCajaDTO;
import com.proyectospa.spa_app.dto.ProductoFacturaDTO;
import com.proyectospa.spa_app.dto.ReporteExistenciaDTO;
import com.proyectospa.spa_app.dto.VentaDetalladaDTO;
import com.proyectospa.spa_app.model.*;
import com.proyectospa.spa_app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    private TurnoRepository turnoRepository;

    // ✅ MÉTODO COUNT OPTIMIZADO PARA DESCUENTOS
    public Map<String, Object> verificarDescuentoClienteFrecuente(Integer clienteId) {
        Usuario usuario = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + clienteId));
        
        LocalDateTime desde = LocalDateTime.now().minusDays(30);
        
        long cantidadTurnos = turnoRepository.countByClienteIdAndFechaAfterAndEstado(
            clienteId, desde.toLocalDate(), EstadoTurno.FINALIZADO);
        
        boolean aplicaDescuento = cantidadTurnos >= 3;
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("clienteId", clienteId);
        respuesta.put("clienteNombre", usuario.getNombre() + " " + usuario.getApellido());
        respuesta.put("turnosUltimoMes", cantidadTurnos);
        respuesta.put("aplicaDescuento", aplicaDescuento);
        respuesta.put("descuento", aplicaDescuento ? "15%" : "0%");
        respuesta.put("fechaConsulta", LocalDateTime.now());
        respuesta.put("periodoEvaluado", "Últimos 30 días");
        respuesta.put("turnosRequeridos", 3);
        respuesta.put("turnosFaltantes", aplicaDescuento ? 0 : Math.max(0, 3 - cantidadTurnos));
        
        System.out.println("🔍 Verificando descuento - Cliente: " + usuario.getNombre() + 
                          ", Turnos: " + cantidadTurnos + ", Aplica: " + aplicaDescuento);
        
        return respuesta;
    }

    // ✅ MÉTODO GUARDAR COMPLETAMENTE CORREGIDO CON DESCUENTOS DE PRODUCTO
    @Transactional
public FacturaDTO guardar(FacturaDTO dto) {
    System.out.println("🚀 INICIANDO guardar factura - Usuario: " + dto.getUsuarioId());
    
    try {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> {
                    System.err.println("❌ Usuario no encontrado: " + dto.getUsuarioId());
                    return new IllegalArgumentException("Usuario no encontrado");
                });
                
        System.out.println("✅ Usuario encontrado: " + usuario.getNombre());

        Factura factura = new Factura();
        factura.setUsuario(usuario);
        
        // Validar método de pago
        try {
            factura.setMetodoPago(Enum.valueOf(MetodoPago.class, dto.getMetodoPago()));
            System.out.println("✅ Método de pago: " + dto.getMetodoPago());
        } catch (Exception e) {
            System.err.println("❌ Error en método de pago: " + dto.getMetodoPago());
            throw new IllegalArgumentException("Método de pago inválido: " + dto.getMetodoPago());
        }
        
        factura.setFecha(LocalDateTime.now());

        BigDecimal subtotalSinDescuentos = BigDecimal.ZERO;
        BigDecimal subtotalConDescuentoProducto = BigDecimal.ZERO;
        BigDecimal descuentoProductosTotal = BigDecimal.ZERO;
        List<FacturaProducto> productosFactura = new ArrayList<>();

        System.out.println("🔍 Validando stock para " + dto.getProductos().size() + " productos...");

        // ✅ PRIMERO: Validar stock
        for (ProductoFacturaDTO pfDTO : dto.getProductos()) {
            Producto producto = productoRepository.findById(pfDTO.getProductoId())
                    .orElseThrow(() -> {
                        System.err.println("❌ Producto no encontrado: " + pfDTO.getProductoId());
                        return new IllegalArgumentException("Producto no encontrado: " + pfDTO.getProductoId());
                    });

            System.out.println("📦 Producto: " + producto.getNombre() + 
                              ", Stock: " + producto.getStock() + 
                              ", Solicitado: " + pfDTO.getCantidad() +
                              ", Descuento: " + producto.getDescuento() + "%");

            if (producto.getStock() < pfDTO.getCantidad()) {
                String error = "Stock insuficiente: " + producto.getNombre() + 
                              " (disponible: " + producto.getStock() + 
                              ", solicitado: " + pfDTO.getCantidad() + ")";
                System.err.println("❌ " + error);
                throw new IllegalArgumentException(error);
            }
        }

        System.out.println("✅ Stock validado correctamente");

        // ✅ SEGUNDO: Procesar productos y calcular subtotales CON DESCUENTOS
        for (ProductoFacturaDTO pfDTO : dto.getProductos()) {
            Producto producto = productoRepository.findById(pfDTO.getProductoId()).get();

            FacturaProducto fp = new FacturaProducto();
            fp.setFactura(factura);
            fp.setProducto(producto);
            fp.setCantidad(pfDTO.getCantidad() != null ? pfDTO.getCantidad() : 1);
            
            // ✅ CALCULAR PRECIO CON DESCUENTO DE PRODUCTO (VERSIÓN BIGDECIMAL)
            BigDecimal precioBase = producto.getPrecio();
            
            // Obtener descuento como BigDecimal
            BigDecimal descuentoProducto = producto.getDescuento();
            if (descuentoProducto == null) {
                descuentoProducto = BigDecimal.ZERO;
            }
            
            // Calcular precio con descuento
            BigDecimal precioConDescuento = precioBase;
            if (descuentoProducto.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal factorDescuento = BigDecimal.ONE.subtract(
                    descuentoProducto.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                );
                precioConDescuento = precioBase.multiply(factorDescuento);
            }
            
            // Subtotal sin descuentos
            BigDecimal subtotalSinDescuento = precioBase.multiply(BigDecimal.valueOf(fp.getCantidad()));
            subtotalSinDescuentos = subtotalSinDescuentos.add(subtotalSinDescuento);
            
            // Subtotal con descuento de producto
            BigDecimal subtotalConDescuento = precioConDescuento.multiply(BigDecimal.valueOf(fp.getCantidad()));
            subtotalConDescuentoProducto = subtotalConDescuentoProducto.add(subtotalConDescuento);
            
            // Descuento total de productos
            BigDecimal descuentoProductoMonto = subtotalSinDescuento.subtract(subtotalConDescuento);
            descuentoProductosTotal = descuentoProductosTotal.add(descuentoProductoMonto);
            
            // Guardar el subtotal CON descuento en la factura producto
            fp.setSubtotal(subtotalConDescuento);
            
            productosFactura.add(fp);

            // ✅ DESCONTAR STOCK
            int stockAnterior = producto.getStock();
            producto.setStock(stockAnterior - fp.getCantidad());
            Producto productoActualizado = productoRepository.save(producto);
            
            System.out.println("📦 Stock actualizado - " + producto.getNombre() + 
                              ": " + stockAnterior + " → " + productoActualizado.getStock() +
                              ", Precio con descuento: " + precioConDescuento);
        }

        // ✅ TERCERO: Calcular IVA sobre el subtotal CON descuentos de producto
        BigDecimal iva = subtotalConDescuentoProducto.multiply(BigDecimal.valueOf(0.21));
        BigDecimal subtotalMasIva = subtotalConDescuentoProducto.add(iva);

        System.out.println("💰 CÁLCULOS INTERMEDIOS:");
        System.out.println("   - Subtotal sin descuentos: " + subtotalSinDescuentos);
        System.out.println("   - Descuento productos: " + descuentoProductosTotal);
        System.out.println("   - Subtotal con descuentos producto: " + subtotalConDescuentoProducto);
        System.out.println("   - IVA (21%): " + iva);
        System.out.println("   - Subtotal + IVA: " + subtotalMasIva);

        // ✅ CUARTO: Aplicar descuentos adicionales
        BigDecimal descuentoAdicional = BigDecimal.ZERO;
        BigDecimal totalAntesDescuentosAdicionales = subtotalMasIva;

        // Descuento por método de pago (10% efectivo) - sobre el total + IVA
        if (factura.getMetodoPago() == MetodoPago.EFECTIVO) {
            BigDecimal descuentoEfectivo = totalAntesDescuentosAdicionales.multiply(BigDecimal.valueOf(0.10));
            descuentoAdicional = descuentoAdicional.add(descuentoEfectivo);
            System.out.println("   - Descuento efectivo (10%): " + descuentoEfectivo);
        }
        
        // ✅ DESCUENTO POR CLIENTE FRECUENTE (15%) - sobre el total + IVA
        if (esClienteFrecuente(usuario)) {
            BigDecimal descuentoFrecuente = totalAntesDescuentosAdicionales.multiply(BigDecimal.valueOf(0.15));
            descuentoAdicional = descuentoAdicional.add(descuentoFrecuente);
            System.out.println("   - Descuento cliente frecuente (15%): " + descuentoFrecuente);
        }

        // ✅ TOTAL FINAL
        BigDecimal totalFinal = totalAntesDescuentosAdicionales.subtract(descuentoAdicional);
        BigDecimal descuentoTotal = descuentoProductosTotal.add(descuentoAdicional);

        factura.setDescuento(descuentoTotal);
        factura.setTotal(totalFinal);
        
        System.out.println("💰 RESUMEN FINAL:");
        System.out.println("   - Descuento total: " + descuentoTotal);
        System.out.println("   - Total final: " + totalFinal);
        
        System.out.println("💾 Guardando factura en base de datos...");
        Factura guardada = facturaRepository.save(factura);
        System.out.println("✅ Factura guardada con ID: " + guardada.getId());
        
        for (FacturaProducto fp : productosFactura) {
            facturaProductoRepository.save(fp);
        }
        System.out.println("✅ Productos de factura guardados");
        
        Factura facturaCompleta = facturaRepository.findById(guardada.getId())
            .orElseThrow(() -> {
                System.err.println("❌ Factura no encontrada después de guardar: " + guardada.getId());
                return new IllegalArgumentException("Factura no encontrada después de guardar");
            });

        System.out.println("🎉 Factura completada exitosamente - ID: " + facturaCompleta.getId());
        return convertirADTO(facturaCompleta);
        
    } catch (Exception e) {
        System.err.println("💥 ERROR CRÍTICO en guardar factura: " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException("Error al procesar la factura: " + e.getMessage(), e);
    }
}

    // ✅ MÉTODO ORIGINAL (no modificar)
    private boolean esClienteFrecuente(Usuario usuario) {
        LocalDateTime desde = LocalDateTime.now().minusDays(30);
        
        long turnosRecientes = turnoRepository.findByClienteId(usuario.getId()).stream()
                .filter(turno -> !turno.getFecha().isBefore(desde.toLocalDate()))
                .filter(turno -> turno.getEstado() == EstadoTurno.FINALIZADO)
                .count();
        
        boolean aplica = turnosRecientes >= 3;
        System.out.println("🎯 esClienteFrecuente - " + usuario.getNombre() + 
                          ": " + turnosRecientes + " turnos, aplica: " + aplica);
        
        return aplica;
    }

    // ✅ MÉTODO CORREGIDO CON STREAM IMPORT
    public List<VentaDetalladaDTO> generarReporteVentasDetallado(LocalDateTime desde, LocalDateTime hasta) {
        List<Factura> facturas = facturaRepository.findByFechaBetween(desde, hasta);
        
        return facturas.stream()
                .flatMap(factura -> {
                    if (factura.getProductos() == null) {
                        return Stream.empty();
                    }
                    return factura.getProductos().stream()
                            .map(fp -> {
                                VentaDetalladaDTO dto = new VentaDetalladaDTO();
                                dto.setFacturaId(factura.getId());
                                dto.setFechaVenta(factura.getFecha());
                                dto.setProductoNombre(fp.getProducto().getNombre());
                                dto.setCategoriaNombre(fp.getProducto().getCategoria() != null ? 
                                    fp.getProducto().getCategoria().getNombre() : "Sin categoría");
                                dto.setCantidad(fp.getCantidad());
                                dto.setPrecioUnitario(fp.getProducto().getPrecio());
                                dto.setSubtotal(fp.getSubtotal());
                                dto.setMetodoPago(factura.getMetodoPago().name());
                                return dto;
                            });
                })
                .collect(Collectors.toList());
    }

    // MÉTODOS EXISTENTES (sin cambios)
    public List<ReporteExistenciaDTO> generarReporteExistencias(Integer categoriaId, String nombreProducto) {
        List<Producto> productos = productoRepository.buscarPorFiltros(categoriaId, nombreProducto);
        
        return productos.stream()
                .map(producto -> {
                    ReporteExistenciaDTO dto = new ReporteExistenciaDTO();
                    dto.setProductoId(producto.getId());
                    dto.setProductoNombre(producto.getNombre());
                    dto.setCategoriaNombre(producto.getCategoria() != null ? 
                        producto.getCategoria().getNombre() : "Sin categoría");
                    dto.setStockActual(producto.getStock());
                    dto.setPrecio(producto.getPrecio());
                    dto.setUltimaActualizacion(LocalDateTime.now());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<FacturaDTO> listarTodas() {
        return facturaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public void eliminar(Integer id) {
        if (!facturaRepository.existsById(id)) {
            throw new IllegalArgumentException("Factura no encontrada con id: " + id);
        }
        facturaRepository.deleteById(id);
    }

    public List<FacturaDTO> listarPorUsuario(Integer usuarioId) {
        return facturaRepository.findByUsuario_Id(usuarioId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    public List<FacturaDTO> reportePorFecha(LocalDateTime desde, LocalDateTime hasta) {
        return facturaRepository.findByFechaBetween(desde, hasta).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

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
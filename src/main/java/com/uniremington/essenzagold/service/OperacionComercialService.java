package com.uniremington.essenzagold.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.uniremington.essenzagold.model.dao.CompraDAO;
import com.uniremington.essenzagold.model.dao.DetalleCompraDAO;
import com.uniremington.essenzagold.model.dao.DetalleVentaDAO;
import com.uniremington.essenzagold.model.dao.ProductoDAO;
import com.uniremington.essenzagold.model.dao.VentaDAO;
import com.uniremington.essenzagold.model.vo.CompraVO;
import com.uniremington.essenzagold.model.vo.DetalleCompraVO;
import com.uniremington.essenzagold.model.vo.DetalleVentaVO;
import com.uniremington.essenzagold.model.vo.VentaVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OperacionComercialService {

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    private final CompraDAO compraDAO;
    private final DetalleCompraDAO detalleCompraDAO;
    private final VentaDAO ventaDAO;
    private final DetalleVentaDAO detalleVentaDAO;
    private final ProductoDAO productoDAO;

    public OperacionComercialService(
            CompraDAO compraDAO,
            DetalleCompraDAO detalleCompraDAO,
            VentaDAO ventaDAO,
            DetalleVentaDAO detalleVentaDAO,
            ProductoDAO productoDAO) {
        this.compraDAO = compraDAO;
        this.detalleCompraDAO = detalleCompraDAO;
        this.ventaDAO = ventaDAO;
        this.detalleVentaDAO = detalleVentaDAO;
        this.productoDAO = productoDAO;
    }

    @Transactional
    public CompraVO registrarCompra(CompraRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La información de la compra es obligatoria.");
        }
        validarCabecera(request.idProveedor(), request.idUsuario(), request.metodoPago(), request.lineas());
        request.lineas().forEach(linea -> {
            if (linea == null) {
                throw new IllegalArgumentException("La compra contiene un detalle vacío.");
            }
            validarLinea(linea.idProducto(), linea.cantidad(), linea.costoUnitario());
        });
        CompraVO compra = compraDAO.insert(new CompraVO(
                null,
                request.idProveedor(),
                request.idUsuario(),
                null,
                request.numeroFactura(),
                BigDecimal.ZERO,
                request.metodoPago(),
                CompraVO.Estado.Recibida,
                request.observaciones()));
        for (LineaCompra linea : request.lineas()) {
            detalleCompraDAO.insert(new DetalleCompraVO(
                    null, compra.idCompra(), linea.idProducto(), linea.cantidad(), linea.costoUnitario(), null));
            productoDAO.synchronizeAvailabilityWithStock(linea.idProducto());
        }
        return compraDAO.findById(compra.idCompra())
                .orElseThrow(() -> new IllegalStateException("No fue posible consultar la compra registrada."));
    }

    @Transactional
    public VentaVO registrarVenta(VentaRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La información de la venta es obligatoria.");
        }
        validarCabecera(request.idUsuario(), request.idUsuario(), request.metodoPago(), request.lineas());
        request.lineas().forEach(linea -> {
            if (linea == null) {
                throw new IllegalArgumentException("La venta contiene un detalle vacío.");
            }
            validarLinea(linea.idProducto(), linea.cantidad(), linea.precioUnitario());
            BigDecimal descuento = linea.descuentoPorcentaje() == null ? BigDecimal.ZERO : linea.descuentoPorcentaje();
            if (descuento.signum() < 0 || descuento.compareTo(CIEN) > 0) {
                throw new IllegalArgumentException("El descuento debe estar entre 0 y 100 por ciento.");
            }
        });
        VentaVO venta = ventaDAO.insert(new VentaVO(
                null,
                request.idUsuario(),
                request.nombreCliente(),
                request.documentoCliente(),
                request.telefonoCliente(),
                null,
                BigDecimal.ZERO,
                request.metodoPago(),
                VentaVO.Estado.Completada,
                request.observaciones()));
        for (LineaVenta linea : request.lineas()) {
            BigDecimal descuento = linea.descuentoPorcentaje() == null ? BigDecimal.ZERO : linea.descuentoPorcentaje();
            BigDecimal precioFinal = linea.precioUnitario()
                    .multiply(CIEN.subtract(descuento))
                    .divide(CIEN, 2, RoundingMode.HALF_UP);
            detalleVentaDAO.insert(new DetalleVentaVO(
                    null, venta.idVenta(), linea.idProducto(), linea.cantidad(), precioFinal, null));
            productoDAO.synchronizeAvailabilityWithStock(linea.idProducto());
        }
        return ventaDAO.findById(venta.idVenta())
                .orElseThrow(() -> new IllegalStateException("No fue posible consultar la venta registrada."));
    }

    private static void validarCabecera(
            Integer idPrincipal,
            Integer idUsuario,
            Enum<?> metodoPago,
            List<?> lineas) {
        if (idPrincipal == null || idPrincipal <= 0 || idUsuario == null || idUsuario <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un proveedor o usuario válido.");
        }
        if (metodoPago == null) {
            throw new IllegalArgumentException("Debe seleccionar un método de pago.");
        }
        if (lineas == null || lineas.isEmpty()) {
            throw new IllegalArgumentException("La operación debe incluir al menos un producto.");
        }
    }

    private static void validarLinea(Integer idProducto, Integer cantidad, BigDecimal precio) {
        if (idProducto == null || idProducto <= 0 || cantidad == null || cantidad <= 0
                || precio == null || precio.signum() < 0) {
            throw new IllegalArgumentException("Cada detalle requiere producto, cantidad y precio válidos.");
        }
    }

    public record CompraRequest(
            Integer idProveedor,
            Integer idUsuario,
            String numeroFactura,
            CompraVO.MetodoPago metodoPago,
            String observaciones,
            List<LineaCompra> lineas) {
    }

    public record LineaCompra(Integer idProducto, Integer cantidad, BigDecimal costoUnitario) {
    }

    public record VentaRequest(
            Integer idUsuario,
            String nombreCliente,
            String documentoCliente,
            String telefonoCliente,
            VentaVO.MetodoPago metodoPago,
            String observaciones,
            List<LineaVenta> lineas) {
    }

    public record LineaVenta(
            Integer idProducto,
            Integer cantidad,
            BigDecimal precioUnitario,
            BigDecimal descuentoPorcentaje) {
    }
}

package com.uniremington.essenzagold.model.vo;

import java.math.BigDecimal;

public record DetalleVentaVO(
        Integer idDetalleVenta,
        Integer idVenta,
        Integer idProducto,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal) {
}
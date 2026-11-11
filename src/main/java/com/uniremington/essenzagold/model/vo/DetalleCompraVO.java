package com.uniremington.essenzagold.model.vo;

import java.math.BigDecimal;

public record DetalleCompraVO(
        Integer idDetalleCompra,
        Integer idCompra,
        Integer idProducto,
        Integer cantidad,
        BigDecimal costoUnitario,
        BigDecimal subtotal) {
}
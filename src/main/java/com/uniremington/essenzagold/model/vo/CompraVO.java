package com.uniremington.essenzagold.model.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CompraVO(
        Integer idCompra,
        Integer idProveedor,
        Integer idUsuario,
        LocalDateTime fechaCompra,
        String numeroFactura,
        BigDecimal total,
        MetodoPago metodoPago,
        Estado estado,
        String observaciones) {

    public enum MetodoPago {
        Efectivo,
        Transferencia,
        Tarjeta,
        Credito
    }

    public enum Estado {
        Pendiente,
        Recibida,
        Cancelada
    }
}
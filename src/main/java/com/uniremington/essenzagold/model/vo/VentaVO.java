package com.uniremington.essenzagold.model.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VentaVO(
        Integer idVenta,
        Integer idUsuario,
        String nombreCliente,
        String documentoCliente,
        String telefonoCliente,
        LocalDateTime fechaVenta,
        BigDecimal total,
        MetodoPago metodoPago,
        Estado estado,
        String observaciones) {

    public enum MetodoPago {
        Efectivo,
        Transferencia,
        Tarjeta,
        Nequi,
        Daviplata
    }

    public enum Estado {
        Completada,
        Anulada
    }
}
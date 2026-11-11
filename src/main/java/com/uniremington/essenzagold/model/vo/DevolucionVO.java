package com.uniremington.essenzagold.model.vo;

import java.time.LocalDateTime;

public record DevolucionVO(
        Integer idDevolucion,
        Integer idDetalleVenta,
        Integer idUsuario,
        LocalDateTime fechaDevolucion,
        Integer cantidad,
        String motivo,
        Estado estado,
        EstadoProducto estadoProducto,
        String observaciones) {

    public enum Estado {
        Solicitada,
        Aprobada,
        Rechazada,
        Finalizada
    }

    public enum EstadoProducto {
        Bueno,
        Dañado,
        Abierto
    }
}
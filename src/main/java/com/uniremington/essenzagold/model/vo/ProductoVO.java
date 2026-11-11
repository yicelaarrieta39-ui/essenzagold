package com.uniremington.essenzagold.model.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductoVO(
        Integer idProducto,
        String codigo,
        String nombre,
        String descripcion,
        Categoria categoria,
        String presentacion,
        BigDecimal capacidadMl,
        String concentracion,
        BigDecimal precioCompra,
        BigDecimal precioVenta,
        Integer stock,
        Integer stockMinimo,
        Estado estado,
        LocalDateTime fechaRegistro) {

    public enum Categoria {
        Perfume,
        Esencia,
        Splash,
        Crema,
        Otros
    }

    public enum Estado {
        Disponible,
        Agotado,
        Inactivo
    }
}
package com.uniremington.essenzagold.model.vo;

import java.time.LocalDateTime;

public record ProveedorVO(
        Integer idProveedor,
        String nombreEmpresa,
        String nit,
        String contacto,
        String telefono,
        String correo,
        String direccion,
        String ciudad,
        Estado estado,
        LocalDateTime fechaRegistro) {

    public enum Estado {
        Activo,
        Inactivo
    }
}
package com.uniremington.essenzagold.model.vo;

import java.time.LocalDateTime;

public record UsuarioVO(
        Integer idUsuario,
        String nombre,
        String apellido,
        String documento,
        String correo,
        String telefono,
        String direccion,
        String contrasena,
        Rol rol,
        Estado estado,
        LocalDateTime fechaRegistro) {

    public enum Rol {
        Administrador,
        Vendedor,
        Almacenista
    }

    public enum Estado {
        Activo,
        Inactivo
    }
}
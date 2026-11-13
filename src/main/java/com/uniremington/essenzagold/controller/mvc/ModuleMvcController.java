package com.uniremington.essenzagold.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ModuleMvcController {

    @GetMapping("/usuarios")
    public String usuarios() {
        return "usuarios/index";
    }

    @GetMapping("/productos")
    public String productos() {
        return "productos/index";
    }

    @GetMapping("/proveedores")
    public String proveedores() {
        return "proveedores/index";
    }

    @GetMapping("/compras")
    public String compras() {
        return "compras/index";
    }

    @GetMapping("/ventas")
    public String ventas() {
        return "ventas/index";
    }

    @GetMapping("/devoluciones")
    public String devoluciones() {
        return "devoluciones/index";
    }
}

package com.uniremington.essenzagold.controller.mvc;

import java.math.BigDecimal;
import java.util.List;

import com.uniremington.essenzagold.model.dao.CompraDAO;
import com.uniremington.essenzagold.model.dao.DevolucionDAO;
import com.uniremington.essenzagold.model.dao.ProductoDAO;
import com.uniremington.essenzagold.model.dao.ProveedorDAO;
import com.uniremington.essenzagold.model.dao.UsuarioDAO;
import com.uniremington.essenzagold.model.dao.VentaDAO;
import com.uniremington.essenzagold.model.vo.ProductoVO;
import com.uniremington.essenzagold.model.vo.VentaVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardMvcController {

    private final ProductoDAO productoDAO;
    private final ProveedorDAO proveedorDAO;
    private final UsuarioDAO usuarioDAO;
    private final CompraDAO compraDAO;
    private final VentaDAO ventaDAO;
    private final DevolucionDAO devolucionDAO;

    public DashboardMvcController(
            ProductoDAO productoDAO,
            ProveedorDAO proveedorDAO,
            UsuarioDAO usuarioDAO,
            CompraDAO compraDAO,
            VentaDAO ventaDAO,
            DevolucionDAO devolucionDAO) {
        this.productoDAO = productoDAO;
        this.proveedorDAO = proveedorDAO;
        this.usuarioDAO = usuarioDAO;
        this.compraDAO = compraDAO;
        this.ventaDAO = ventaDAO;
        this.devolucionDAO = devolucionDAO;
    }

    @GetMapping({"/", "/app"})
    public String dashboard(Model model) {
        var productos = productoDAO.findAll();
        var compras = compraDAO.findAll();
        var ventas = ventaDAO.findAll();
        List<ProductoVO> inventarioBajo = productos.stream()
                .filter(producto -> producto.estado() != ProductoVO.Estado.Inactivo)
                .filter(producto -> producto.stock() <= producto.stockMinimo())
                .toList();
        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("totalVentas", ventas.size());
        model.addAttribute("totalCompras", compras.size());
        model.addAttribute("totalUsuarios", usuarioDAO.findAll().size());
        model.addAttribute("ventasFacturadas", ventas.stream()
                .filter(venta -> venta.estado() == VentaVO.Estado.Completada)
                .map(VentaVO::total)
                .filter(total -> total != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        model.addAttribute("inventarioBajo", inventarioBajo);
        return "dashboard/index";
    }
}

package com.uniremington.essenzagold.controller.res;

import java.net.URI;
import java.util.List;

import com.uniremington.essenzagold.model.dao.DetalleVentaDAO;
import com.uniremington.essenzagold.model.vo.DetalleVentaVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/detalles-ventas")
public class DetalleVentaRestController {

    private final DetalleVentaDAO detalleVentaDAO;

    public DetalleVentaRestController(DetalleVentaDAO detalleVentaDAO) {
        this.detalleVentaDAO = detalleVentaDAO;
    }

    @GetMapping
    public List<DetalleVentaVO> findAll() {
        return detalleVentaDAO.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleVentaVO> findById(@PathVariable Integer id) {
        return detalleVentaDAO.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DetalleVentaVO> create(@RequestBody DetalleVentaVO detalle) {
        DetalleVentaVO created = detalleVentaDAO.insert(detalle);
        return ResponseEntity.created(URI.create("/api/detalles-ventas/" + created.idDetalleVenta())).body(created);
    }
}

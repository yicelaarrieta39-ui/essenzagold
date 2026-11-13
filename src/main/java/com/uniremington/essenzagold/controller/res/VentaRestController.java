package com.uniremington.essenzagold.controller.res;

import java.net.URI;
import java.util.List;

import com.uniremington.essenzagold.model.dao.VentaDAO;
import com.uniremington.essenzagold.model.vo.VentaVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ventas")
public class VentaRestController {

    private final VentaDAO ventaDAO;

    public VentaRestController(VentaDAO ventaDAO) {
        this.ventaDAO = ventaDAO;
    }

    @GetMapping
    public List<VentaVO> findAll() {
        return ventaDAO.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaVO> findById(@PathVariable Integer id) {
        return ventaDAO.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<VentaVO> create(@RequestBody VentaVO venta) {
        VentaVO created = ventaDAO.insert(venta);
        return ResponseEntity.created(URI.create("/api/ventas/" + created.idVenta())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody VentaVO venta) {
        if (!id.equals(venta.idVenta())) {
            return ResponseEntity.badRequest().build();
        }
        return ventaDAO.update(venta) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

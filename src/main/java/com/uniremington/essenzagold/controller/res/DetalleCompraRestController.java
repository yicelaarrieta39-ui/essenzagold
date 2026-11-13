package com.uniremington.essenzagold.controller.res;

import java.net.URI;
import java.util.List;

import com.uniremington.essenzagold.model.dao.DetalleCompraDAO;
import com.uniremington.essenzagold.model.vo.DetalleCompraVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/detalles-compras")
public class DetalleCompraRestController {

    private final DetalleCompraDAO detalleCompraDAO;

    public DetalleCompraRestController(DetalleCompraDAO detalleCompraDAO) {
        this.detalleCompraDAO = detalleCompraDAO;
    }

    @GetMapping
    public List<DetalleCompraVO> findAll() {
        return detalleCompraDAO.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleCompraVO> findById(@PathVariable Integer id) {
        return detalleCompraDAO.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DetalleCompraVO> create(@RequestBody DetalleCompraVO detalle) {
        DetalleCompraVO created = detalleCompraDAO.insert(detalle);
        return ResponseEntity.created(URI.create("/api/detalles-compras/" + created.idDetalleCompra())).body(created);
    }
}

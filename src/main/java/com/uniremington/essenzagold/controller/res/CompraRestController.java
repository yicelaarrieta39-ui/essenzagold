package com.uniremington.essenzagold.controller.res;

import java.net.URI;
import java.util.List;

import com.uniremington.essenzagold.model.dao.CompraDAO;
import com.uniremington.essenzagold.model.vo.CompraVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/compras")
public class CompraRestController {

    private final CompraDAO compraDAO;

    public CompraRestController(CompraDAO compraDAO) {
        this.compraDAO = compraDAO;
    }

    @GetMapping
    public List<CompraVO> findAll() {
        return compraDAO.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraVO> findById(@PathVariable Integer id) {
        return compraDAO.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CompraVO> create(@RequestBody CompraVO compra) {
        CompraVO created = compraDAO.insert(compra);
        return ResponseEntity.created(URI.create("/api/compras/" + created.idCompra())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody CompraVO compra) {
        if (!id.equals(compra.idCompra())) {
            return ResponseEntity.badRequest().build();
        }
        return compraDAO.update(compra) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

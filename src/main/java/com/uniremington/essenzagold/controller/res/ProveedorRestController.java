package com.uniremington.essenzagold.controller.res;

import java.net.URI;
import java.util.List;

import com.uniremington.essenzagold.model.dao.ProveedorDAO;
import com.uniremington.essenzagold.model.vo.ProveedorVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorRestController {

    private final ProveedorDAO proveedorDAO;

    public ProveedorRestController(ProveedorDAO proveedorDAO) {
        this.proveedorDAO = proveedorDAO;
    }

    @GetMapping
    public List<ProveedorVO> findAll() {
        return proveedorDAO.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorVO> findById(@PathVariable Integer id) {
        return proveedorDAO.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProveedorVO> create(@RequestBody ProveedorVO proveedor) {
        ProveedorVO created = proveedorDAO.insert(proveedor);
        return ResponseEntity.created(URI.create("/api/proveedores/" + created.idProveedor())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody ProveedorVO proveedor) {
        if (!id.equals(proveedor.idProveedor())) {
            return ResponseEntity.badRequest().build();
        }
        return proveedorDAO.update(proveedor) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        return proveedorDAO.deleteById(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

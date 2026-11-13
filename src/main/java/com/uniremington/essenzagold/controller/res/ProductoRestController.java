package com.uniremington.essenzagold.controller.res;

import java.net.URI;
import java.util.List;

import com.uniremington.essenzagold.model.dao.ProductoDAO;
import com.uniremington.essenzagold.model.vo.ProductoVO;
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
@RequestMapping("/api/productos")
public class ProductoRestController {

    private final ProductoDAO productoDAO;

    public ProductoRestController(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    @GetMapping
    public List<ProductoVO> findAll() {
        return productoDAO.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoVO> findById(@PathVariable Integer id) {
        return productoDAO.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductoVO> create(@RequestBody ProductoVO producto) {
        ProductoVO created = productoDAO.insert(producto);
        return ResponseEntity.created(URI.create("/api/productos/" + created.idProducto())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCatalog(@PathVariable Integer id, @RequestBody ProductoVO producto) {
        if (!id.equals(producto.idProducto())) {
            return ResponseEntity.badRequest().build();
        }
        return productoDAO.updateCatalog(producto)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        return productoDAO.deleteById(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

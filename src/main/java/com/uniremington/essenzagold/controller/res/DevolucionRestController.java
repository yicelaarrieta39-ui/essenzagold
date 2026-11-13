package com.uniremington.essenzagold.controller.res;

import java.net.URI;
import java.util.List;

import com.uniremington.essenzagold.model.dao.DevolucionDAO;
import com.uniremington.essenzagold.model.vo.DevolucionVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/devoluciones")
public class DevolucionRestController {

    private final DevolucionDAO devolucionDAO;

    public DevolucionRestController(DevolucionDAO devolucionDAO) {
        this.devolucionDAO = devolucionDAO;
    }

    @GetMapping
    public List<DevolucionVO> findAll() {
        return devolucionDAO.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DevolucionVO> findById(@PathVariable Integer id) {
        return devolucionDAO.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DevolucionVO> create(@RequestBody DevolucionVO devolucion) {
        DevolucionVO created = devolucionDAO.insert(devolucion);
        return ResponseEntity.created(URI.create("/api/devoluciones/" + created.idDevolucion())).body(created);
    }
}

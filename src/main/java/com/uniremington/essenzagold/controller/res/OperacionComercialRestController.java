package com.uniremington.essenzagold.controller.res;

import com.uniremington.essenzagold.model.vo.CompraVO;
import com.uniremington.essenzagold.model.vo.VentaVO;
import com.uniremington.essenzagold.service.OperacionComercialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/operaciones")
public class OperacionComercialRestController {

    private final OperacionComercialService operacionComercialService;

    public OperacionComercialRestController(OperacionComercialService operacionComercialService) {
        this.operacionComercialService = operacionComercialService;
    }

    @PostMapping("/compras")
    public ResponseEntity<CompraVO> registrarCompra(
            @RequestBody OperacionComercialService.CompraRequest request) {
        return ResponseEntity.ok(operacionComercialService.registrarCompra(request));
    }

    @PostMapping("/ventas")
    public ResponseEntity<VentaVO> registrarVenta(
            @RequestBody OperacionComercialService.VentaRequest request) {
        return ResponseEntity.ok(operacionComercialService.registrarVenta(request));
    }
}

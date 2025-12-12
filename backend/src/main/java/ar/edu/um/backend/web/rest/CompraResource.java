package ar.edu.um.backend.web.rest;

import ar.edu.um.backend.domain.Venta;
import ar.edu.um.backend.service.OrquestadorVentaService;
import ar.edu.um.backend.service.dto.CompraDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CompraResource {

    private final OrquestadorVentaService orquestadorService;

    public CompraResource(OrquestadorVentaService orquestadorService) {
        this.orquestadorService = orquestadorService;
    }

    @PostMapping("/comprar")
    public ResponseEntity<?> realizarCompra(@RequestBody CompraDTO compra) {
        try {

            String usuarioActual = "admin";

            Venta venta = orquestadorService.comprarEntrada(
                compra.getEventoId(),
                compra.getFila(),
                compra.getColumna(),
                usuarioActual
            );

            return ResponseEntity.ok(venta);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("❌ Error en la compra: " + e.getMessage());
        }
    }
}

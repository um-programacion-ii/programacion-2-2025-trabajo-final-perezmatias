package ar.edu.um.proxy.web;

import ar.edu.um.proxy.dto.SolicitudBloqueoDTO;
import ar.edu.um.proxy.service.CatedraApiService;
import ar.edu.um.proxy.service.CatedraRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catedra")
public class CatedraController {

    @Autowired
    private CatedraRedisService redisService;

    @Autowired
    private CatedraApiService apiService;

    @GetMapping("/asientos/{id}")
    public ResponseEntity<String> getAsientos(@PathVariable String id) {
        String resultado = redisService.obtenerAsientos(id);

        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/bloquear")
    public ResponseEntity<Boolean> bloquearAsiento(@RequestBody SolicitudBloqueoDTO solicitud) {
        boolean resultado = apiService.bloquearAsiento(solicitud);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("PROXY OPERATIVO");
    }
}
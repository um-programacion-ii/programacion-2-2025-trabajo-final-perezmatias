package ar.edu.um.backend.web.rest;

import ar.edu.um.backend.service.SincronizacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sincronizar")
public class SincronizacionResource {

    private final SincronizacionService sincronizacionService;

    public SincronizacionResource(SincronizacionService sincronizacionService) {
        this.sincronizacionService = sincronizacionService;
    }

    @PostMapping("/eventos")
    public ResponseEntity<String> sincronizar() {
        sincronizacionService.sincronizarEventos();
        return ResponseEntity.ok("Sincronización de eventos completada. Revisa la consola.");
    }
}

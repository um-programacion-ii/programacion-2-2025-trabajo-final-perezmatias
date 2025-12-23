package ar.edu.um.proxy.controller;

import ar.edu.um.proxy.dto.EventoDTO;
import ar.edu.um.proxy.service.CatedraService;
import ar.edu.um.proxy.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/proxy")
public class ProxyController {

    private final CatedraService catedraService;
    private final RedisService redisService;

    public ProxyController(CatedraService catedraService, RedisService redisService) {
        this.catedraService = catedraService;
        this.redisService = redisService;
    }

    @GetMapping("/eventos")
    public ResponseEntity<List<EventoDTO>> getEventos() {
        log.info("📱 App solicitando eventos...");

        // 1. Intentamos actualizar datos frescos desde la Cátedra
        // (Si falla, el servicio maneja el error y no rompe nada)
        catedraService.sincronizarEventos();

        // 2. Buscamos lo que haya en Redis (ya sea nuevo o viejo si la Cátedra falló)
        List<EventoDTO> eventos = redisService.obtenerEventosGuardados();

        if (eventos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(eventos);
    }
}
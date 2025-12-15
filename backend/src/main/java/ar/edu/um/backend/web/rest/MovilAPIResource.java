package ar.edu.um.backend.web.rest;

import ar.edu.um.backend.domain.Evento;
import ar.edu.um.backend.domain.Venta;
import ar.edu.um.backend.repository.EventoRepository;
import ar.edu.um.backend.repository.VentaRepository;
import ar.edu.um.backend.web.rest.dto.VentaMovilDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/endpoints/v1")
@Transactional
public class MovilAPIResource {

    private final Logger log = LoggerFactory.getLogger(MovilAPIResource.class);

    private final EventoRepository eventoRepository;
    private final VentaRepository ventaRepository;

    public MovilAPIResource(EventoRepository eventoRepository, VentaRepository ventaRepository) {
        this.eventoRepository = eventoRepository;
        this.ventaRepository = ventaRepository;
    }

    @GetMapping("/eventos-resumidos")
    public List<Evento> getEventosResumidos() {
        return eventoRepository.findAll();
    }

    @GetMapping("/eventos")
    public List<Evento> getEventosCompletos() {
        return eventoRepository.findAll();
    }

    @GetMapping("/evento/{id}")
    public ResponseEntity<Evento> getEventoDetalle(@PathVariable Long id) {
        Optional<Evento> evento = eventoRepository.findById(id);
        return evento.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/bloquear-asientos")
    public ResponseEntity<?> bloquearAsientos(@RequestBody Object bloqueoDTO) {
        log.info("Simulando bloqueo de asientos: {}", bloqueoDTO);
        return ResponseEntity.ok("{\"resultado\": true, \"descripcion\": \"Asientos bloqueados (SIMULADO)\"}");
    }

    @PostMapping("/realizar-venta")
    public ResponseEntity<?> realizarVenta(@RequestBody VentaMovilDTO ventaDTO) {
        log.debug("Solicitud de venta recibida para evento ID: {}", ventaDTO.getEventoId());

        Optional<Evento> eventoOp = eventoRepository.findById(ventaDTO.getEventoId());
        if (eventoOp.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"resultado\": false, \"descripcion\": \"Evento no encontrado\"}");
        }

        Venta nuevaVenta = new Venta();

        if (ventaDTO.getFecha() != null) {
            nuevaVenta.setFechaVenta(ventaDTO.getFecha().toInstant());
        } else {
            nuevaVenta.setFechaVenta(Instant.now());
        }

        nuevaVenta.setTotal(ventaDTO.getPrecioVenta());
        nuevaVenta.setEvento(eventoOp.get());

        Venta ventaGuardada = ventaRepository.save(nuevaVenta);

        log.info("Venta guardada localmente con ID: {}", ventaGuardada.getId());

        return ResponseEntity.ok()
            .body("{\"resultado\": true, \"ventaId\": " + ventaGuardada.getId() + ", \"descripcion\": \"Venta registrada localmente\"}");
    }

    @GetMapping("/listar-ventas")
    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    @GetMapping("/listar-venta/{id}")
    public ResponseEntity<Venta> getVentaDetalle(@PathVariable Long id) {
        return ventaRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}

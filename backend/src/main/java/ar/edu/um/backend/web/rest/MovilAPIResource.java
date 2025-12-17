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

    // Ya no necesitamos UserRepository porque tu tabla Venta NO usa usuarios del sistema
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
        return ResponseEntity.ok("{\"resultado\": true, \"descripcion\": \"Asientos bloqueados (SIMULADO)\"}");
    }

    @PostMapping("/realizar-venta")
    public ResponseEntity<?> realizarVenta(@RequestBody VentaMovilDTO ventaDTO) {
        log.info("💰 PROCESANDO VENTA - Comprador: {} | DNI: {}", ventaDTO.getNombreComprador(), ventaDTO.getDniComprador());

        // 1. Validar Evento
        Optional<Evento> eventoOp = eventoRepository.findById(ventaDTO.getEventoId());
        if (eventoOp.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"resultado\": false, \"descripcion\": \"Evento no encontrado\"}");
        }

        // 2. Crear la Venta y llenar los campos OBLIGATORIOS de tu entidad
        Venta nuevaVenta = new Venta();

        // Fecha
        if (ventaDTO.getFecha() != null) {
            nuevaVenta.setFechaVenta(ventaDTO.getFecha().toInstant());
        } else {
            nuevaVenta.setFechaVenta(Instant.now());
        }

        nuevaVenta.setTotal(ventaDTO.getPrecioVenta());
        nuevaVenta.setEvento(eventoOp.get());

        // --- AQUÍ ESTABA LA CLAVE ---
        // Llenamos los campos que son @NotNull en tu Venta.java

        // Nombre del Comprador
        if (ventaDTO.getNombreComprador() != null) {
            nuevaVenta.setNombreComprador(ventaDTO.getNombreComprador());
        } else {
            nuevaVenta.setNombreComprador("Consumidor Final");
        }

        // DNI del Comprador
        if (ventaDTO.getDniComprador() != null) {
            nuevaVenta.setDniComprador(ventaDTO.getDniComprador());
        } else {
            nuevaVenta.setDniComprador("00000000");
        }

        // Lista de Asientos (La convertimos a String porque en BD es varchar)
        if (ventaDTO.getAsientos() != null) {
            nuevaVenta.setAsientos(ventaDTO.getAsientos().toString());
        } else {
            nuevaVenta.setAsientos("[]");
        }

        try {
            // Guardar en Base de Datos
            Venta ventaGuardada = ventaRepository.save(nuevaVenta);
            log.info("✅ VENTA EXITOSA. ID Generado: {}", ventaGuardada.getId());

            return ResponseEntity.ok()
                .body("{\"resultado\": true, \"ventaId\": " + ventaGuardada.getId() + ", \"descripcion\": \"Venta registrada correctamente\"}");
        } catch (Exception e) {
            log.error("❌ Error al guardar en base de datos: ", e);
            return ResponseEntity.internalServerError()
                .body("{\"resultado\": false, \"descripcion\": \"Error BD: " + e.getMessage() + "\"}");
        }
    }
}

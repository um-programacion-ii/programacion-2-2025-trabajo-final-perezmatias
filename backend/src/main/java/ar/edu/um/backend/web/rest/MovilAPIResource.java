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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // --- GETTERS ---
    @GetMapping("/eventos-resumidos")
    public List<Evento> getEventosResumidos() { return eventoRepository.findAll(); }

    @GetMapping("/eventos")
    public List<Evento> getEventosCompletos() { return eventoRepository.findAll(); }

    @GetMapping("/evento/{id}")
    public ResponseEntity<Evento> getEventoDetalle(@PathVariable Long id) {
        Optional<Evento> evento = eventoRepository.findById(id);
        return evento.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // --- OBTENER ASIENTOS OCUPADOS ---
    @GetMapping("/ocupados/{id}")
    public ResponseEntity<List<SimpleAsientoDTO>> getAsientosOcupados(@PathVariable Long id) {
        List<Venta> ventas = ventaRepository.findAllByEventoId(id);
        List<SimpleAsientoDTO> ocupados = new ArrayList<>();

        // Regex que busca "fila=X" y "columna=Y" en el texto
        Pattern pattern = Pattern.compile("fila[=:](\\d+).*?columna[=:](\\d+)");

        for (Venta venta : ventas) {
            String texto = venta.getAsientos();
            if (texto != null) {
                Matcher matcher = pattern.matcher(texto);
                while (matcher.find()) {
                    SimpleAsientoDTO dto = new SimpleAsientoDTO();
                    dto.fila = Integer.parseInt(matcher.group(1));
                    dto.columna = Integer.parseInt(matcher.group(2));
                    ocupados.add(dto);
                }
            }
        }
        return ResponseEntity.ok(ocupados);
    }

    // Clase interna para devolver solo fila/columna
    public static class SimpleAsientoDTO {
        public int fila;
        public int columna;
    }

    // --- VENTA (MODO KIOSCO) ---
    @PostMapping("/realizar-venta")
    public ResponseEntity<?> realizarVenta(@RequestBody VentaMovilDTO ventaDTO) {
        log.info("💰 VENTA - Cliente: {} | DNI: {}", ventaDTO.getNombreComprador(), ventaDTO.getDniComprador());

        Optional<Evento> eventoOp = eventoRepository.findById(ventaDTO.getEventoId());
        if (eventoOp.isEmpty()) return ResponseEntity.badRequest().body("Evento no encontrado");

        Venta nuevaVenta = new Venta();
        nuevaVenta.setFechaVenta(ventaDTO.getFecha() != null ? ventaDTO.getFecha().toInstant() : Instant.now());
        nuevaVenta.setTotal(ventaDTO.getPrecioVenta());
        nuevaVenta.setEvento(eventoOp.get());

        // Llenamos datos obligatorios
        nuevaVenta.setNombreComprador(ventaDTO.getNombreComprador() != null ? ventaDTO.getNombreComprador() : "Anónimo");
        nuevaVenta.setDniComprador(ventaDTO.getDniComprador() != null ? ventaDTO.getDniComprador() : "0");

        // --- 🛠️ CORRECCIÓN: Guardar formato legible ---
        if (ventaDTO.getAsientos() != null) {
            StringBuilder sb = new StringBuilder();
            // Iteramos los asientos y creamos un texto tipo "[fila=1, columna=2]..."
            for (VentaMovilDTO.AsientoDTO asiento : ventaDTO.getAsientos()) {
                sb.append("[fila=").append(asiento.getFila())
                    .append(", columna=").append(asiento.getColumna())
                    .append("] ");
            }
            nuevaVenta.setAsientos(sb.toString());
        } else {
            nuevaVenta.setAsientos("[]");
        }
        // ----------------------------------------------

        try {
            Venta guardada = ventaRepository.save(nuevaVenta);
            return ResponseEntity.ok("{\"resultado\": true, \"ventaId\": " + guardada.getId() + "}");
        } catch (Exception e) {
            log.error("Error BD: ", e);
            return ResponseEntity.internalServerError().body("{\"resultado\": false, \"descripcion\": \"" + e.getMessage() + "\"}");
        }
    }
}

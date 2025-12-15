package ar.edu.um.backend.web.rest;

import ar.edu.um.backend.domain.Evento;
import ar.edu.um.backend.repository.EventoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/endpoints/v1")
public class MovilAPIResource {

    private final Logger log = LoggerFactory.getLogger(MovilAPIResource.class);

    private final EventoRepository eventoRepository;

    public MovilAPIResource(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @GetMapping("/eventos-resumidos")
    public List<Evento> getEventosResumidos() {
        log.debug("REST request to get Eventos Resumidos for Mobile App");

        return eventoRepository.findAll();
    }

    @GetMapping("/eventos")
    public List<Evento> getEventosCompletos() {
        log.debug("REST request to get All Eventos for Mobile App");
        return eventoRepository.findAll();
    }

    @GetMapping("/evento/{id}")
    public ResponseEntity<Evento> getEventoDetalle(@PathVariable Long id) {
        log.debug("REST request to get Evento : {}", id);
        Optional<Evento> evento = eventoRepository.findById(id);
        
        return evento.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}

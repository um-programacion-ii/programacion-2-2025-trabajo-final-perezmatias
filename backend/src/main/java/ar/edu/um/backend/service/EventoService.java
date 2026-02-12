package ar.edu.um.backend.service;

import ar.edu.um.backend.domain.Evento;
import ar.edu.um.backend.repository.EventoRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ar.edu.um.backend.domain.Evento}.
 */
@Service
@Transactional
public class EventoService {

    private static final Logger LOG = LoggerFactory.getLogger(EventoService.class);

    private final EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    /**
     * Save a evento.
     *
     * @param evento the entity to save.
     * @return the persisted entity.
     */
    public Evento save(Evento evento) {
        LOG.debug("Request to save Evento (con chequeo de duplicados): {}", evento);

        // 1. Verificamos si el evento tiene título
        if (evento.getTitulo() != null) {

            // 2. Buscamos en la BD si ya existe ese título
            Optional<Evento> existente = eventoRepository.findOneByTitulo(evento.getTitulo());

            if (existente.isPresent()) {
                // 3. Si existe, ¡LE ROBAMOS EL ID!
                LOG.debug("Evento duplicado detectado: {}. Actualizando ID: {}", evento.getTitulo(), existente.get().getId());
                evento.setId(existente.get().getId());
            }
        }

        // 4. Guardamos (Si tenía ID hace Update, si no, hace Insert)
        return eventoRepository.save(evento);
    }

    /**
     * Update a evento.
     *
     * @param evento the entity to save.
     * @return the persisted entity.
     */
    public Evento update(Evento evento) {
        LOG.debug("Request to update Evento : {}", evento);
        return eventoRepository.save(evento);
    }

    /**
     * Partially update a evento.
     *
     * @param evento the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Evento> partialUpdate(Evento evento) {
        LOG.debug("Request to partially update Evento : {}", evento);

        return eventoRepository
            .findById(evento.getId())
            .map(existingEvento -> {
                if (evento.getTitulo() != null) {
                    existingEvento.setTitulo(evento.getTitulo());
                }
                if (evento.getDescripcion() != null) {
                    existingEvento.setDescripcion(evento.getDescripcion());
                }
                if (evento.getFechaHora() != null) {
                    existingEvento.setFechaHora(evento.getFechaHora());
                }
                if (evento.getUbicacion() != null) {
                    existingEvento.setUbicacion(evento.getUbicacion());
                }
                if (evento.getPrecio() != null) {
                    existingEvento.setPrecio(evento.getPrecio());
                }
                if (evento.getCantidadFilas() != null) {
                    existingEvento.setCantidadFilas(evento.getCantidadFilas());
                }
                if (evento.getCantidadColumnas() != null) {
                    existingEvento.setCantidadColumnas(evento.getCantidadColumnas());
                }

                return existingEvento;
            })
            .map(eventoRepository::save);
    }

    /**
     * Get all the eventos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Evento> findAll(Pageable pageable) {
        LOG.debug("Request to get all Eventos");
        return eventoRepository.findAll(pageable);
    }

    /**
     * Get one evento by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Evento> findOne(Long id) {
        LOG.debug("Request to get Evento : {}", id);
        return eventoRepository.findById(id);
    }

    /**
     * Delete the evento by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Evento : {}", id);
        eventoRepository.deleteById(id);
    }
}

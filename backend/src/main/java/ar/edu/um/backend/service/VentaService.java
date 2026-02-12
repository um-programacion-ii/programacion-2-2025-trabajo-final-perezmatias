package ar.edu.um.backend.service;

import ar.edu.um.backend.domain.Venta;
import ar.edu.um.backend.repository.VentaRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ar.edu.um.backend.domain.Venta}.
 */
@Service
@Transactional
public class VentaService {

    private static final Logger LOG = LoggerFactory.getLogger(VentaService.class);

    private final VentaRepository ventaRepository;

    public VentaService(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    /**
     * Save a venta.
     *
     * @param venta the entity to save.
     * @return the persisted entity.
     */
    public Venta save(Venta venta) {
        LOG.debug("Request to save Venta : {}", venta);
        return ventaRepository.save(venta);
    }

    /**
     * Update a venta.
     *
     * @param venta the entity to save.
     * @return the persisted entity.
     */
    public Venta update(Venta venta) {
        LOG.debug("Request to update Venta : {}", venta);
        return ventaRepository.save(venta);
    }

    /**
     * Partially update a venta.
     *
     * @param venta the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Venta> partialUpdate(Venta venta) {
        LOG.debug("Request to partially update Venta : {}", venta);

        return ventaRepository
            .findById(venta.getId())
            .map(existingVenta -> {
                if (venta.getFechaVenta() != null) {
                    existingVenta.setFechaVenta(venta.getFechaVenta());
                }
                if (venta.getTotal() != null) {
                    existingVenta.setTotal(venta.getTotal());
                }
                if (venta.getAsientos() != null) {
                    existingVenta.setAsientos(venta.getAsientos());
                }
                if (venta.getNombreComprador() != null) {
                    existingVenta.setNombreComprador(venta.getNombreComprador());
                }
                if (venta.getDniComprador() != null) {
                    existingVenta.setDniComprador(venta.getDniComprador());
                }

                return existingVenta;
            })
            .map(ventaRepository::save);
    }

    /**
     * Get all the ventas.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Venta> findAll(Pageable pageable) {
        LOG.debug("Request to get all Ventas");
        return ventaRepository.findAll(pageable);
    }

    /**
     * Get all the ventas with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<Venta> findAllWithEagerRelationships(Pageable pageable) {
        return ventaRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one venta by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Venta> findOne(Long id) {
        LOG.debug("Request to get Venta : {}", id);
        return ventaRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the venta by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Venta : {}", id);
        ventaRepository.deleteById(id);
    }
}

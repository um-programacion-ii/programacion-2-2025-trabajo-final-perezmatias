package ar.edu.um.backend.service;

import ar.edu.um.backend.domain.Evento;
import ar.edu.um.backend.repository.EventoRepository;
import ar.edu.um.backend.service.dto.EventoCatedraDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class SincronizacionService {

    private final CatedraService catedraService;
    private final EventoRepository eventoRepository;

    public SincronizacionService(CatedraService catedraService, EventoRepository eventoRepository) {
        this.catedraService = catedraService;
        this.eventoRepository = eventoRepository;
    }

    public void sincronizarEventos() {
        System.out.println("🔄 Iniciando sincronización de eventos...");

        // 1. Traer datos de la API externa
        List<EventoCatedraDTO> eventosExternos = catedraService.obtenerEventos();

        // 2. Procesar cada evento
        for (EventoCatedraDTO dto : eventosExternos) {
            // Buscamos si ya existe un evento con ese título
            Evento eventoExistente = eventoRepository.findAll().stream()
                .filter(e -> e.getTitulo().equals(dto.getTitulo()))
                .findFirst()
                .orElse(null);

            try {
                if (eventoExistente == null) {
                    // CASO 1: NUEVO EVENTO
                    Evento nuevoEvento = mapToEntity(dto);
                    eventoRepository.save(nuevoEvento);
                    System.out.println("✅ Evento NUEVO guardado: " + dto.getTitulo() +
                        " [" + nuevoEvento.getCantidadFilas() + "x" + nuevoEvento.getCantidadColumnas() + "]");
                } else {
                    // CASO 2: EVENTO EXISTENTE (Verificamos si hay que corregir tamaño)
                    boolean cambio = false;

                    // Verificamos filas
                    if (dto.getCantidadFilas() != null && !dto.getCantidadFilas().equals(eventoExistente.getCantidadFilas())) {
                        eventoExistente.setCantidadFilas(dto.getCantidadFilas());
                        cambio = true;
                    }

                    // Verificamos columnas
                    if (dto.getCantidadColumnas() != null && !dto.getCantidadColumnas().equals(eventoExistente.getCantidadColumnas())) {
                        eventoExistente.setCantidadColumnas(dto.getCantidadColumnas());
                        cambio = true;
                    }

                    if (cambio) {
                        eventoRepository.save(eventoExistente);
                        System.out.println("🔄 Evento ACTUALIZADO (Dimensiones corregidas): " + dto.getTitulo() +
                            " -> Ahora es " + eventoExistente.getCantidadFilas() + "x" + eventoExistente.getCantidadColumnas());
                    } else {
                        System.out.println("ℹ️ Evento al día (sin cambios de tamaño): " + dto.getTitulo());
                    }
                }
            } catch (Exception e) {
                System.err.println("⚠️ Error procesando evento " + dto.getTitulo() + ": " + e.getMessage());
            }
        }
        System.out.println("🏁 Sincronización finalizada.");
    }

    // Método auxiliar para convertir el DTO de la cátedra a nuestra Entidad
    private Evento mapToEntity(EventoCatedraDTO dto) {
        Evento evento = new Evento();

        evento.setTitulo(dto.getTitulo());
        evento.setDescripcion(dto.getDescripcion());
        evento.setUbicacion(dto.getUbicacion());
        evento.setPrecio(dto.getPrecio());

        // Manejo de fechas
        try {
            if (dto.getFechaHoraString() != null) {
                evento.setFechaHora(Instant.parse(dto.getFechaHoraString()));
            } else {
                evento.setFechaHora(Instant.now());
            }
        } catch (Exception e) {
            evento.setFechaHora(Instant.now()); // Fallback
        }

        // Asignamos dimensiones (usando fallback a 10 si vienen vacías)
        if (dto.getCantidadFilas() != null) {
            evento.setCantidadFilas(dto.getCantidadFilas());
        } else {
            evento.setCantidadFilas(10);
        }

        if (dto.getCantidadColumnas() != null) {
            evento.setCantidadColumnas(dto.getCantidadColumnas());
        } else {
            evento.setCantidadColumnas(10);
        }

        return evento;
    }
}

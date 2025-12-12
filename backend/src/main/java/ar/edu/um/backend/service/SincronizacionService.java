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

        // 2. Guardarlos en BD
        for (EventoCatedraDTO dto : eventosExternos) {
            boolean existe = eventoRepository.findAll().stream()
                .anyMatch(e -> e.getTitulo().equals(dto.getTitulo()));

            if (!existe) {
                Evento nuevoEvento = new Evento();
                nuevoEvento.setTitulo(dto.getTitulo());
                nuevoEvento.setDescripcion(dto.getDescripcion());
                nuevoEvento.setUbicacion(dto.getUbicacion());
                nuevoEvento.setPrecio(dto.getPrecio());

                try {
                    nuevoEvento.setFechaHora(Instant.parse(dto.getFechaHoraString()));
                } catch (Exception e) {
                    nuevoEvento.setFechaHora(Instant.now());
                }

                eventoRepository.save(nuevoEvento);
                System.out.println("✅ Evento guardado: " + dto.getTitulo());
            }
        }
        System.out.println("🏁 Sincronización finalizada.");
    }
}

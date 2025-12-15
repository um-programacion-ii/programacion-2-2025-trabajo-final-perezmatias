package ar.edu.um.backend.service;

import ar.edu.um.backend.domain.Evento;
import ar.edu.um.backend.domain.Venta;
import ar.edu.um.backend.repository.EventoRepository;
import ar.edu.um.backend.repository.VentaRepository;
import ar.edu.um.backend.service.dto.AsientoDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrquestadorVentaService {

    private final ProxyService proxyService;
    private final EventoRepository eventoRepository;
    private final VentaRepository ventaRepository;

    public OrquestadorVentaService(ProxyService proxyService,
                                   EventoRepository eventoRepository,
                                   VentaRepository ventaRepository) {
        this.proxyService = proxyService;
        this.eventoRepository = eventoRepository;
        this.ventaRepository = ventaRepository;
    }

    public Venta comprarEntrada(Long eventoId, int fila, int columna, String nombreComprador, String dniComprador) {
        Evento evento = eventoRepository.findById(eventoId)
            .orElseThrow(() -> new RuntimeException("El evento no existe en la base de datos local"));

        List<AsientoDTO> mapaActual = proxyService.obtenerAsientos(eventoId);

        Optional<AsientoDTO> asientoDeseado = mapaActual.stream()
            .filter(a -> a.getFila() == fila && a.getColumna() == columna)
            .findFirst();

        if (asientoDeseado.isPresent()) {
            if (!asientoDeseado.get().estaDisponible()) {
                throw new RuntimeException("El asiento F" + fila + "-C" + columna + " ya está ocupado.");
            }
        }

        System.out.println("🔒 Solicitando bloqueo para DNI: " + dniComprador);
        boolean bloqueado = proxyService.bloquearAsiento(eventoId, fila, columna, dniComprador);

        if (!bloqueado) {
            throw new RuntimeException("⛔ No se pudo bloquear el asiento en la Cátedra.");
        }

        Venta nuevaVenta = new Venta();
        nuevaVenta.setFechaVenta(Instant.now());
        nuevaVenta.setTotal(evento.getPrecio());
        nuevaVenta.setAsientos("F" + fila + "-C" + columna);

        nuevaVenta.setNombreComprador(nombreComprador);
        nuevaVenta.setDniComprador(dniComprador);

        nuevaVenta.setEvento(evento);

        return ventaRepository.save(nuevaVenta);
    }
}

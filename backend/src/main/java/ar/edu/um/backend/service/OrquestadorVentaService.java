package ar.edu.um.backend.service;

import ar.edu.um.backend.domain.Evento;
import ar.edu.um.backend.domain.Venta;
import ar.edu.um.backend.repository.EventoRepository;
import ar.edu.um.backend.repository.VentaRepository;
import ar.edu.um.backend.service.dto.AsientoDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
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

    public List<AsientoDTO> obtenerMapaDeAsientos(Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
            .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        List<AsientoDTO> ocupados = proxyService.obtenerAsientos(eventoId);
        if (ocupados == null) ocupados = new ArrayList<>();

        List<AsientoDTO> mapaCompleto = new ArrayList<>();

        int maxFilas = (evento.getCantidadFilas() != null) ? evento.getCantidadFilas() : 10;
        int maxCols = (evento.getCantidadColumnas() != null) ? evento.getCantidadColumnas() : 10;

        if (maxFilas > 50) maxFilas = 50;
        if (maxCols > 50) maxCols = 50;

        System.out.println("📊 Generando mapa de " + maxFilas + "x" + maxCols + " para Evento " + eventoId);

        for (int f = 1; f <= maxFilas; f++) {
            for (int c = 1; c <= maxCols; c++) {

                AsientoDTO asiento = new AsientoDTO();
                asiento.setFila(f);
                asiento.setColumna(c);
                asiento.setEstado("Libre");

                int finalF = f;
                int finalC = c;

                Optional<AsientoDTO> ocupado = ocupados.stream()
                    .filter(o -> o.getFila() == finalF && o.getColumna() == finalC)
                    .findFirst();

                if (ocupado.isPresent()) {
                    AsientoDTO infoReal = ocupado.get();
                    if (infoReal.estaDisponible()) {
                        asiento.setEstado("Libre");
                    } else {
                        asiento.setEstado(infoReal.getEstado());
                    }
                }

                mapaCompleto.add(asiento);
            }
        }

        return mapaCompleto;
    }

    public Venta comprarEntrada(Long eventoId, int fila, int columna, String nombreComprador, String dniComprador) {
        Evento evento = eventoRepository.findById(eventoId)
            .orElseThrow(() -> new RuntimeException("El evento no existe"));

        List<AsientoDTO> ocupados = proxyService.obtenerAsientos(eventoId);
        if (ocupados == null) ocupados = new ArrayList<>();

        Optional<AsientoDTO> asientoDeseado = ocupados.stream()
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

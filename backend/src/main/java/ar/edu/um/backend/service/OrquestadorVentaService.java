package ar.edu.um.backend.service;

import ar.edu.um.backend.domain.Evento;
import ar.edu.um.backend.domain.User;
import ar.edu.um.backend.domain.Venta;
import ar.edu.um.backend.repository.EventoRepository;
import ar.edu.um.backend.repository.UserRepository;
import ar.edu.um.backend.repository.VentaRepository;
import ar.edu.um.backend.service.dto.AsientoDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrquestadorVentaService {

    private final ProxyService proxyService;
    private final EventoRepository eventoRepository;
    private final VentaRepository ventaRepository;
    private final UserRepository userRepository;

    public OrquestadorVentaService(ProxyService proxyService,
                                   EventoRepository eventoRepository,
                                   VentaRepository ventaRepository,
                                   UserRepository userRepository) {
        this.proxyService = proxyService;
        this.eventoRepository = eventoRepository;
        this.ventaRepository = ventaRepository;
        this.userRepository = userRepository;
    }


    public Venta comprarEntrada(Long eventoId, int fila, int columna, String loginUsuario) {
        Evento evento = eventoRepository.findById(eventoId)
            .orElseThrow(() -> new RuntimeException("El evento no existe en la base de datos local"));

        List<AsientoDTO> mapaActual = proxyService.obtenerAsientos(eventoId);

        Optional<AsientoDTO> asientoDeseado = mapaActual.stream()
            .filter(a -> a.getFila() == fila && a.getColumna() == columna)
            .findFirst();

        if (asientoDeseado.isPresent()) {

            if (!asientoDeseado.get().estaDisponible()) {
                throw new RuntimeException("El asiento F" + fila + "-C" + columna + " ya está ocupado o reservado.");
            }
        }



        User comprador = userRepository.findOneByLogin(loginUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + loginUsuario));


        System.out.println("🔒 Solicitando bloqueo a la Cátedra para F" + fila + "-C" + columna + "...");
        boolean bloqueado = proxyService.bloquearAsiento(eventoId, fila, columna, loginUsuario);

        if (!bloqueado) {
            throw new RuntimeException("⛔ No se pudo bloquear el asiento en el servidor de la Cátedra. Puede que alguien más lo haya ganado.");
        }
        System.out.println("✅ Asiento bloqueado exitosamente. Procediendo al cobro.");

        Venta nuevaVenta = new Venta();
        nuevaVenta.setFechaVenta(Instant.now());
        nuevaVenta.setTotal(evento.getPrecio());
        nuevaVenta.setAsientos("F" + fila + "-C" + columna);
        nuevaVenta.setEvento(evento);
        nuevaVenta.setUser(comprador);

        return ventaRepository.save(nuevaVenta);
    }
}

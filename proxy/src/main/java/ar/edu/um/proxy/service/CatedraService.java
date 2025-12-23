package ar.edu.um.proxy.service;

import ar.edu.um.proxy.dto.EventoDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class CatedraService {

    private final RestTemplate restTemplate;
    private final String catedraUrl;
    private final String token;
    private final ObjectMapper objectMapper;
    private final RedisService redisService;

    public CatedraService(RestTemplate restTemplate,
                          @Value("${catedra.url}") String catedraUrl,
                          @Value("${catedra.token}") String token,
                          ObjectMapper objectMapper,
                          RedisService redisService) {
        this.restTemplate = restTemplate;
        this.catedraUrl = catedraUrl;
        this.token = token;
        this.objectMapper = objectMapper;
        this.redisService = redisService;
    }

    public void sincronizarEventos() {
        String urlCompleta = catedraUrl.endsWith("/") ? catedraUrl + "api/endpoints/v1/eventos" : catedraUrl + "/api/endpoints/v1/eventos";
        log.info("📡 Conectando a Catedra...");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    urlCompleta, HttpMethod.GET, new HttpEntity<>(headers), String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String json = response.getBody();

                // 1. Convertir JSON a Objetos Java
                List<EventoDTO> eventos = objectMapper.readValue(json, new TypeReference<List<EventoDTO>>() {});
                log.info("✅ Recibidos {} eventos desde la Cátedra.", eventos.size());

                // 2. Guardar en Redis
                redisService.guardarEventos(eventos);

                // 3. Imprimir títulos para verificar
                eventos.forEach(e -> System.out.println("   🎟️ Evento: " + e.getTitulo() + " ($" + e.getPrecioEntrada() + ")"));
            }

        } catch (Exception e) {
            log.error("❌ Error en sincronización: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
package ar.edu.um.proxy.service;

import ar.edu.um.proxy.dto.SolicitudBloqueoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CatedraApiService {

    @Value("${catedra.url}")
    private String urlCatedra;

    @Value("${catedra.token}")
    private String token;

    private final RestTemplate restTemplate;

    public CatedraApiService() {
        this.restTemplate = new RestTemplate();
    }

    public boolean bloquearAsiento(SolicitudBloqueoDTO solicitud) {
        String endpoint = urlCatedra + "/api/endpoints/v1/bloquear-asientos";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            java.util.Map<String, Integer> asientoMap = new java.util.HashMap<>();
            asientoMap.put("fila", solicitud.getFila());
            asientoMap.put("columna", solicitud.getColumna());

            java.util.List<java.util.Map<String, Integer>> listaAsientos = new java.util.ArrayList<>();
            listaAsientos.add(asientoMap);

            java.util.Map<String, Object> bodyCatedra = new java.util.HashMap<>();
            bodyCatedra.put("eventoId", solicitud.getEventoId());
            bodyCatedra.put("asientos", listaAsientos);

            HttpEntity<java.util.Map<String, Object>> entity = new HttpEntity<>(bodyCatedra, headers);

            System.out.println("📞 Llamando a Cátedra (URL OFICIAL): " + endpoint);


            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, entity, String.class);


            System.out.println("📩 Respuesta Cátedra: " + response.getBody());


            return response.getBody() != null && response.getBody().contains("true");

        } catch (Exception e) {
            System.err.println("❌ Error al llamar a la Cátedra: " + e.getMessage());
            return false;
        }
    }
}
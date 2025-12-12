package ar.edu.um.backend.service;

import ar.edu.um.backend.service.dto.EventoCatedraDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class CatedraService {

    @Value("${catedra.url}")
    private String urlCatedra;

    @Value("${catedra.token}")
    private String token;

    private final RestTemplate restTemplate;

    public CatedraService() {
        this.restTemplate = new RestTemplate();
    }

    public List<EventoCatedraDTO> obtenerEventos() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String endpoint = urlCatedra + "/api/endpoints/v1/eventos";

        try {
            ResponseEntity<List<EventoCatedraDTO>> response = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<EventoCatedraDTO>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            System.err.println("❌ Error sincronizando con cátedra: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}

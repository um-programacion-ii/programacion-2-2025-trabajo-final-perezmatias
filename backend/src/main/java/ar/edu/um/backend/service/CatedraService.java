package ar.edu.um.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    // Método para pedir la lista de eventos a la cátedra
    public String obtenerEventos() {
        // 1. Preparamos la cabecera con tu Token (Authorization: Bearer TU_TOKEN)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 2. Construimos la URL completa (según el PDF: /api/endpoints/v1/eventos)
        String endpoint = urlCatedra + "/api/endpoints/v1/eventos";

        // 3. Hacemos la llamada
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                entity,
                String.class
            );
            return response.getBody();
        } catch (Exception e) {
            return "Error al conectar con Cátedra: " + e.getMessage();
        }
    }
}

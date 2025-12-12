package ar.edu.um.backend.service;

import ar.edu.um.backend.service.dto.AsientoDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProxyService {

    @Value("${proxy.url}")
    private String urlProxy;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ProxyService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public List<AsientoDTO> obtenerAsientos(Long eventoId) {
        String endpoint = urlProxy + "/api/catedra/asientos/" + eventoId;

        try {
            String jsonRespuesta = restTemplate.getForObject(endpoint, String.class);

            JsonNode root = objectMapper.readTree(jsonRespuesta);
            JsonNode listaAsientosNode = root.path("asientos");

            if (listaAsientosNode.isMissingNode()) {
                return new ArrayList<>();
            }

            return objectMapper.readerFor(new TypeReference<List<AsientoDTO>>(){})
                .readValue(listaAsientosNode);

        } catch (Exception e) {
            System.err.println("⚠️ Error conectando con Proxy: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}

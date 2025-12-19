package ar.edu.um.proxy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

//Communicacion con el Backend Local

@Service
public class BackendClient {

    private final String backendUrl = "http://localhost:8080/api";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String localToken = null;

//Autenticación contra el JHipster local

    private void login() {
        try {
            String loginUrl = "http://localhost:8080/api/authenticate";
            Map<String, Object> loginMap = new HashMap<>();
            loginMap.put("username", "admin");
            loginMap.put("password", "admin");
            loginMap.put("rememberMe", false);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(loginMap, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(loginUrl, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                this.localToken = (String) response.getBody().get("id_token");
                System.out.println("🔑 ¡Login exitoso en Backend Local! Token renovado.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error fatal haciendo login en Backend Local: " + e.getMessage());
        }
    }

//Recibe los datos de la catedra y los corrige

    public void guardarEventoEnBackend(String jsonEvento) {
        if (localToken == null) login();

        try {
            JsonNode rootNode = objectMapper.readTree(jsonEvento);

            if (rootNode instanceof ObjectNode) {
                ObjectNode object = (ObjectNode) rootNode;

                if (object.has("id")) {
                    object.remove("id");
                }

                if (!object.has("cantidadFilas") || object.get("cantidadFilas").isNull()) object.put("cantidadFilas", 10);
                if (!object.has("cantidadColumnas") || object.get("cantidadColumnas").isNull()) object.put("cantidadColumnas", 10);
                if (!object.has("precio") || object.get("precio").isNull()) object.put("precio", 1000.0);
                if (!object.has("fechaHora") || object.get("fechaHora").isNull()) object.put("fechaHora", "2025-12-31T20:00:00Z");
                if (!object.has("titulo") || object.get("titulo").isNull()) object.put("titulo", "Evento Importado");

                jsonEvento = object.toString();
            }

//Envia los datos limpios al Backend

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + localToken);

            HttpEntity<String> request = new HttpEntity<>(jsonEvento, headers);
            String url = backendUrl + "/eventos";

            restTemplate.postForEntity(url, request, String.class);
            System.out.println("✅ Evento sincronizado correctamente!");

        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("401")) {
                System.out.println("⚠️ Token expirado. Reintentando login...");
                login();
            }
            System.err.println("❌ Error al guardar en Backend Local: " + e.getMessage());
        }
    }
}
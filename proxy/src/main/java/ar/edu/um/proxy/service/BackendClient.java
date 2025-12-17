package ar.edu.um.proxy.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class BackendClient {

    private final String backendUrl = "http://localhost:8080/api";
    private final RestTemplate restTemplate = new RestTemplate();

    private String localToken = null;

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

    public void guardarEventoEnBackend(String jsonEvento) {
        if (localToken == null) {
            login();
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + localToken); // Usamos el token local

            HttpEntity<String> request = new HttpEntity<>(jsonEvento, headers);

            String url = backendUrl + "/eventos";

            restTemplate.postForEntity(url, request, String.class);
            System.out.println("✅ Evento sincronizado con el Backend Local!");

        } catch (Exception e) {
            if (e.getMessage().contains("401")) {
                System.out.println("⚠️ Token expirado o inválido. Reintentando login...");
                login();
            }
            System.err.println("❌ Error al guardar en Backend Local: " + e.getMessage());
        }
    }
}
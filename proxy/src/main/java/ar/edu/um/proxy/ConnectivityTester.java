package ar.edu.um.proxy;

import ar.edu.um.proxy.service.BackendClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ConnectivityTester implements CommandLineRunner {

    @Autowired
    private BackendClient backendClient;

    // Leemos la URL y el Token del application.properties
    @Value("${catedra.url}")
    private String catedraUrl;

    @Value("${catedra.token}")
    private String catedraToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> PROXY INICIADO <<<");
        // Al arrancar, intentamos sincronizar inmediatamente para no esperar a Kafka
        sincronizarConCatedra();
    }

    @KafkaListener(topics = "eventos-actualizacion", groupId = "${spring.kafka.consumer.group-id}")
    public void escucharKafka(String mensaje) {
        System.out.println("🔔 KAFKA ALERTA: El profesor notificó cambios.");
        // NO usamos el 'mensaje' porque es solo texto plano.
        // Disparamos la búsqueda de datos reales:
        sincronizarConCatedra();
    }

    private void sincronizarConCatedra() {
        System.out.println("🔄 Conectando a la API del Profesor para descargar eventos...");

        try {
            // 1. Preparamos la cabecera con el Token del Profesor
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + catedraToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 2. Consultamos el endpoint GET /eventos de la Cátedra
            String url = catedraUrl + "/api/eventos";

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // 3. Leemos el JSON que devolvió el profesor (es una lista [])
                JsonNode eventosArray = objectMapper.readTree(response.getBody());

                System.out.println("📦 La Cátedra tiene " + eventosArray.size() + " eventos.");

                // 4. Recorremos y enviamos CADA evento a tu Backend Local
                for (JsonNode evento : eventosArray) {
                    try {
                        // Convertimos el objeto JSON a String para que tu Backend lo entienda
                        String eventoJson = evento.toString();
                        backendClient.guardarEventoEnBackend(eventoJson);
                    } catch (Exception e) {
                        System.err.println("⚠️ Error al guardar un evento específico: " + e.getMessage());
                    }
                }
                System.out.println("✅ Sincronización Finalizada con éxito.");

            } else {
                System.err.println("❌ La Cátedra devolvió error: " + response.getStatusCode());
            }

        } catch (Exception e) {
            System.err.println("❌ Error de conexión con la Cátedra: " + e.getMessage());
            System.err.println("   (Verifica que la IP en application.properties sea correcta)");
        }
    }
}
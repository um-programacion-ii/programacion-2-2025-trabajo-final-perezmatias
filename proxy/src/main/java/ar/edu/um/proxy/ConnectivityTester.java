package ar.edu.um.proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ConnectivityTester implements CommandLineRunner {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--------------------------------------------------");
        System.out.println(">>> INICIANDO PRUEBA DE CONEXIÓN A LA CÁTEDRA <<<");
        System.out.println("--------------------------------------------------");

        try {
            System.out.println("🔍 Buscando claves 'evento_*' en Redis...");
            java.util.Set<String> keys = redisTemplate.keys("evento_*");

            if (keys != null && !keys.isEmpty()) {
                System.out.println("✅ ¡ENCONTRÉ ESTAS CLAVES!:");
                for (String key : keys) {
                    System.out.println("   🔑 " + key);
                    System.out.println("      Contenido: " + redisTemplate.opsForValue().get(key));
                }
            } else {
                System.out.println("📭 El servidor Redis está accesible, pero NO tiene eventos cargados ahora mismo.");
                System.out.println("💡 Tip: Prueba llamar al endpoint 'forzar-actualizacion' más tarde.");
            }
        } catch (Exception e) {
            System.err.println("❌ ERROR CRÍTICO EN REDIS: " + e.getMessage());
            System.err.println("Revisar conexión ZeroTier o IP en application.properties");
        }
    }


    @KafkaListener(topics = "eventos-actualizacion", groupId = "${spring.kafka.consumer.group-id}")
    public void escucharKafka(String mensaje) {
        System.out.println("🔔 KAFKA ALERTA: Se recibió un mensaje del tópico 'eventos-actualizacion':");
        System.out.println(mensaje);
    }
}
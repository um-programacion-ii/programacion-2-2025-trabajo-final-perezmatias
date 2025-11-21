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

            String datosEvento = redisTemplate.opsForValue().get("evento_1");

            if (datosEvento != null) {
                System.out.println("✅ REDIS CONECTADO: Se recuperaron datos del evento_1:");
                System.out.println(datosEvento);
            } else {
                System.out.println("⚠️ REDIS CONECTADO PERO SIN DATOS: La clave 'evento_1' no devolvió nada.");
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
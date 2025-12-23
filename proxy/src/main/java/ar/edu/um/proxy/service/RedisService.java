package ar.edu.um.proxy.service;

import ar.edu.um.proxy.dto.EventoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void guardarEventos(List<EventoDTO> eventos) {
        try {
            // 1. Guardar la lista completa como JSON String (Para listados rápidos)
            String jsonLista = objectMapper.writeValueAsString(eventos);
            redisTemplate.opsForValue().set("eventos:todos", jsonLista, Duration.ofHours(1));
            log.info("💾 Guardados {} eventos en Redis clave 'eventos:todos'", eventos.size());

            // 2. Guardar cada evento individualmente (Para detalles)
            for (EventoDTO evento : eventos) {
                String jsonEvento = objectMapper.writeValueAsString(evento);
                String clave = "evento:" + evento.getId();
                redisTemplate.opsForValue().set(clave, jsonEvento, Duration.ofHours(2));
            }

        } catch (Exception e) {
            log.error("❌ Error guardando en Redis: {}", e.getMessage());
        }
    }
    public List<EventoDTO> obtenerEventosGuardados() {
        try {
            String json = redisTemplate.opsForValue().get("eventos:todos");
            if (json != null) {
                // Convertimos el texto de Redis de vuelta a Objetos Java
                return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<List<EventoDTO>>() {});
            }
        } catch (Exception e) {
            log.error("❌ Error leyendo de Redis: {}", e.getMessage());
        }
        return List.of(); // Retorna lista vacía si falla o no hay datos
    }
}
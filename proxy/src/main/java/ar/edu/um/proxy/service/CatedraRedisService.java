package ar.edu.um.proxy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

//Obtener asientos

@Service
public class CatedraRedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public String obtenerAsientos(String eventoId) {
        String key = "evento_" + eventoId;
        String datos = redisTemplate.opsForValue().get(key);

        if (datos == null) {
            return "{}";
        }
        return datos;
    }
}
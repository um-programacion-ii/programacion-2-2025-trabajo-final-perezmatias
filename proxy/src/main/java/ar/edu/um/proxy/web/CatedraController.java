package ar.edu.um.proxy.web;

import ar.edu.um.proxy.dto.SolicitudBloqueoDTO;
import ar.edu.um.proxy.service.CatedraApiService;
import ar.edu.um.proxy.service.CatedraRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ar.edu.um.proxy.service.BackendClient;

//Controlador REST del Proxy

@RestController
@RequestMapping("/api/catedra")
public class CatedraController {

    @Autowired
    private CatedraRedisService redisService;

    @Autowired
    private CatedraApiService apiService;

    @Autowired
    private BackendClient backendClient;

    @GetMapping("/asientos/{id}")
    public ResponseEntity<String> getAsientos(@PathVariable String id) {
        String resultado = redisService.obtenerAsientos(id);

        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/bloquear")
    public ResponseEntity<Boolean> bloquearAsiento(@RequestBody SolicitudBloqueoDTO solicitud) {
        boolean resultado = apiService.bloquearAsiento(solicitud);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("PROXY OPERATIVO");
    }

    @PostMapping("/test-sync")
    public ResponseEntity<String> probarSincronizacion() {
        String jsonSimulado = "{"
                + "\"titulo\": \"Evento de Prueba Sincronizado\","
                + "\"descripcion\": \"Este evento fue forzado desde el Proxy para probar la conexion\","
                + "\"precio\": 1500.0,"
                + "\"ubicacion\": \"Laboratorio de Informatica\","
                + "\"fechaHora\": \"2025-12-25T20:00:00Z\","
                + "\"cantidadFilas\": 10,"
                + "\"cantidadColumnas\": 10"
                + "}";

        System.out.println("🧪 Iniciando prueba manual de sincronización...");
        backendClient.guardarEventoEnBackend(jsonSimulado);

        return ResponseEntity.ok("Prueba disparada. Revisa la consola del Backend.");
    }
}
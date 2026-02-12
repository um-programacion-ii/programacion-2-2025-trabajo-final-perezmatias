package ar.edu.um.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class EventoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Evento getEventoSample1() {
        return new Evento()
            .id(1L)
            .titulo("titulo1")
            .descripcion("descripcion1")
            .ubicacion("ubicacion1")
            .cantidadFilas(1)
            .cantidadColumnas(1);
    }

    public static Evento getEventoSample2() {
        return new Evento()
            .id(2L)
            .titulo("titulo2")
            .descripcion("descripcion2")
            .ubicacion("ubicacion2")
            .cantidadFilas(2)
            .cantidadColumnas(2);
    }

    public static Evento getEventoRandomSampleGenerator() {
        return new Evento()
            .id(longCount.incrementAndGet())
            .titulo(UUID.randomUUID().toString())
            .descripcion(UUID.randomUUID().toString())
            .ubicacion(UUID.randomUUID().toString())
            .cantidadFilas(intCount.incrementAndGet())
            .cantidadColumnas(intCount.incrementAndGet());
    }
}

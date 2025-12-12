package ar.edu.um.backend.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AsientoDTO {
    private int fila;
    private int columna;
    private String estado; // "Libre", "Reservado", "Vendido"
    private String fechaExpiracion;

    @JsonIgnore
    public boolean estaDisponible() {
        if ("Libre".equalsIgnoreCase(this.estado)) {
            return true;
        }

        if ("Vendido".equalsIgnoreCase(this.estado)) {
            return false;
        }

        if (fechaExpiracion != null && !fechaExpiracion.isEmpty()) {
            try {
                Instant fechaLimite = Instant.parse(fechaExpiracion);
                Instant ahora = Instant.now();


                return ahora.isAfter(fechaLimite);
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }

    // --- Getters y Setters ---
    public int getFila() { return fila; }
    public void setFila(int fila) { this.fila = fila; }

    public int getColumna() { return columna; }
    public void setColumna(int columna) { this.columna = columna; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(String fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
}

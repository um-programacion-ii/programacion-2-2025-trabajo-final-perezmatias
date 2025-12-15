package ar.edu.um.backend.web.rest.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;


public class VentaMovilDTO {
    private Long eventoId;
    private ZonedDateTime fecha;
    private BigDecimal precioVenta;
    private List<AsientoDTO> asientos;

    // Getters y Setters
    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }

    public ZonedDateTime getFecha() { return fecha; }
    public void setFecha(ZonedDateTime fecha) { this.fecha = fecha; }

    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }

    public List<AsientoDTO> getAsientos() { return asientos; }
    public void setAsientos(List<AsientoDTO> asientos) { this.asientos = asientos; }

    public static class AsientoDTO {
        private Integer fila;
        private Integer columna;
        private String persona;

        public Integer getFila() { return fila; }
        public void setFila(Integer fila) { this.fila = fila; }

        public Integer getColumna() { return columna; }
        public void setColumna(Integer columna) { this.columna = columna; }

        public String getPersona() { return persona; }
        public void setPersona(String persona) { this.persona = persona; }
    }
}

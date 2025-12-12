package ar.edu.um.backend.service.dto;

public class SolicitudBloqueoDTO {
    private Long eventoId;
    private int fila;
    private int columna;
    private String usuario;

    // Constructor vacío
    public SolicitudBloqueoDTO() {}

    // Constructor completo
    public SolicitudBloqueoDTO(Long eventoId, int fila, int columna, String usuario) {
        this.eventoId = eventoId;
        this.fila = fila;
        this.columna = columna;
        this.usuario = usuario;
    }

    // Getters y Setters
    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }

    public int getFila() { return fila; }
    public void setFila(int fila) { this.fila = fila; }

    public int getColumna() { return columna; }
    public void setColumna(int columna) { this.columna = columna; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
}

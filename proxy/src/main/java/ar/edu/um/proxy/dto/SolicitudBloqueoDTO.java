package ar.edu.um.proxy.dto;

public class SolicitudBloqueoDTO {
    private Long eventoId;
    private int fila;
    private int columna;
    private String usuario;

    // Constructores, Getters y Setters
    public SolicitudBloqueoDTO() {}

    public SolicitudBloqueoDTO(Long eventoId, int fila, int columna, String usuario) {
        this.eventoId = eventoId;
        this.fila = fila;
        this.columna = columna;
        this.usuario = usuario;
    }

    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }

    public int getFila() { return fila; }
    public void setFila(int fila) { this.fila = fila; }

    public int getColumna() { return columna; }
    public void setColumna(int columna) { this.columna = columna; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
}
package ar.edu.um.backend.service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.math.BigDecimal;
import java.time.Instant;

public class EventoCatedraDTO {
    private Long id;
    private String titulo;
    private String descripcion;

    @JsonAlias("fechaHora")
    private String fechaHoraString;

    private String ubicacion;
    private BigDecimal precio;
    private String imagen;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFechaHoraString() { return fechaHoraString; }
    public void setFechaHoraString(String fechaHoraString) { this.fechaHoraString = fechaHoraString; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
}

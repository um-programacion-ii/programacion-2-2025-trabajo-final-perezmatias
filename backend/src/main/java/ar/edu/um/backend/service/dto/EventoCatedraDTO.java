package ar.edu.um.backend.service.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventoCatedraDTO {

    private Long id;
    private String titulo;
    private String descripcion;

    @JsonAlias({"fecha", "fechaHora"})
    private String fechaHoraString;

    @JsonAlias({"direccion", "ubicacion"})
    private String ubicacion;

    @JsonAlias("precioEntrada")
    private BigDecimal precio;

    private String imagen;

    @JsonAlias("filaAsientos")
    private Integer cantidadFilas;

    @JsonAlias({"columnAsientos", "columnaAsientos"})
    private Integer cantidadColumnas;

    // --- Getters y Setters ---
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

    public Integer getCantidadFilas() { return cantidadFilas; }
    public void setCantidadFilas(Integer cantidadFilas) { this.cantidadFilas = cantidadFilas; }

    public Integer getCantidadColumnas() { return cantidadColumnas; }
    public void setCantidadColumnas(Integer cantidadColumnas) { this.cantidadColumnas = cantidadColumnas; }
}

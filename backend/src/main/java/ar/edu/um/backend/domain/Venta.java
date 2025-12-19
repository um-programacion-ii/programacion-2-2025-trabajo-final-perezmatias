package ar.edu.um.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Venta.
 */
@Entity
@Table(name = "venta")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Venta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "fecha_venta", nullable = false)
    private Instant fechaVenta;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "total", precision = 21, scale = 2, nullable = false)
    private BigDecimal total;

    @NotNull
    @Column(name = "asientos", nullable = false, length = 5000)
    private String asientos;

    @NotNull
    @Column(name = "nombre_comprador", nullable = false)
    private String nombreComprador;

    @NotNull
    @Column(name = "dni_comprador", nullable = false)
    private String dniComprador;

    @ManyToOne(optional = false)
    @NotNull
    private Evento evento;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Venta id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getFechaVenta() {
        return this.fechaVenta;
    }

    public Venta fechaVenta(Instant fechaVenta) {
        this.setFechaVenta(fechaVenta);
        return this;
    }

    public void setFechaVenta(Instant fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getTotal() {
        return this.total;
    }

    public Venta total(BigDecimal total) {
        this.setTotal(total);
        return this;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getAsientos() {
        return this.asientos;
    }

    public Venta asientos(String asientos) {
        this.setAsientos(asientos);
        return this;
    }

    public void setAsientos(String asientos) {
        this.asientos = asientos;
    }

    public String getNombreComprador() {
        return this.nombreComprador;
    }

    public Venta nombreComprador(String nombreComprador) {
        this.setNombreComprador(nombreComprador);
        return this;
    }

    public void setNombreComprador(String nombreComprador) {
        this.nombreComprador = nombreComprador;
    }

    public String getDniComprador() {
        return this.dniComprador;
    }

    public Venta dniComprador(String dniComprador) {
        this.setDniComprador(dniComprador);
        return this;
    }

    public void setDniComprador(String dniComprador) {
        this.dniComprador = dniComprador;
    }

    public Evento getEvento() {
        return this.evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public Venta evento(Evento evento) {
        this.setEvento(evento);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Venta)) {
            return false;
        }
        return getId() != null && getId().equals(((Venta) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Venta{" +
            "id=" + getId() +
            ", fechaVenta='" + getFechaVenta() + "'" +
            ", total=" + getTotal() +
            ", asientos='" + getAsientos() + "'" +
            ", nombreComprador='" + getNombreComprador() + "'" +
            ", dniComprador='" + getDniComprador() + "'" +
            "}";
    }
}

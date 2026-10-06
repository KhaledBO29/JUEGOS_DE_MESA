package edu.ucc.juegosdemesa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_prestamos")
public class HistorialPrestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prestamo_id", nullable = false)
    private Prestamo prestamo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoEventoPrestamo tipo;

    @Column(nullable = false, length = 30)
    private String carnetActor;

    @Column(length = 1000)
    private String detalle;

    @Column(nullable = false)
    private LocalDateTime fecha;

    protected HistorialPrestamo() {
    }

    public HistorialPrestamo(TipoEventoPrestamo tipo, String carnetActor, String detalle) {
        this.tipo = tipo;
        this.carnetActor = carnetActor;
        this.detalle = detalle;
        this.fecha = LocalDateTime.now();
    }

    void asignarPrestamo(Prestamo prestamo) {
        this.prestamo = prestamo;
    }

    public Long getId() {
        return id;
    }

    public TipoEventoPrestamo getTipo() {
        return tipo;
    }

    public String getCarnetActor() {
        return carnetActor;
    }

    public String getDetalle() {
        return detalle;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}

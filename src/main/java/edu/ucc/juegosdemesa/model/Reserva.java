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
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ejemplar_id", nullable = false)
    private Ejemplar ejemplar;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado;

    @Column(nullable = false)
    private LocalDateTime fechaReserva;

    @Column(nullable = false)
    private LocalDateTime fechaExpiracion;

    protected Reserva() {
    }

    public Reserva(Ejemplar ejemplar, Usuario usuario, LocalDateTime fechaReserva,
                   LocalDateTime fechaExpiracion) {
        this.ejemplar = ejemplar;
        this.usuario = usuario;
        this.fechaReserva = fechaReserva;
        this.fechaExpiracion = fechaExpiracion;
        this.estado = EstadoReserva.ACTIVA;
    }

    public boolean estaVencida(LocalDateTime ahora) {
        return estado == EstadoReserva.ACTIVA && !fechaExpiracion.isAfter(ahora);
    }

    public void cambiarEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public Ejemplar getEjemplar() {
        return ejemplar;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }
}

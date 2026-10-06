package edu.ucc.juegosdemesa.model;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ejemplares", uniqueConstraints =
        @UniqueConstraint(name = "uk_ejemplar_codigo_serie", columnNames = "codigo_serie"))
public class Ejemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "codigo_serie", nullable = false, length = 50)
    private String codigoSerie;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoEjemplar estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "juego_id", nullable = false)
    private Juego juego;

    @OneToMany(mappedBy = "ejemplar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Componente> componentes = new ArrayList<>();

    protected Ejemplar() {
    }

    public Ejemplar(String codigoSerie, EstadoEjemplar estado, Juego juego) {
        this.codigoSerie = codigoSerie;
        this.estado = estado;
        this.juego = juego;
    }

    public void agregarComponente(Componente componente) {
        componentes.add(componente);
        componente.asignarEjemplar(this);
    }

    public void actualizarEstado(EstadoEjemplar estado) {
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public String getCodigoSerie() {
        return codigoSerie;
    }

    public EstadoEjemplar getEstado() {
        return estado;
    }

    public Juego getJuego() {
        return juego;
    }

    public List<Componente> getComponentes() {
        return List.copyOf(componentes);
    }
}

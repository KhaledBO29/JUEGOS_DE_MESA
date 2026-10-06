package edu.ucc.juegosdemesa.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "juegos")
public class Juego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String titulo;

    @NotBlank
    @Column(nullable = false, length = 80)
    private String categoria;

    @Positive
    @Column(nullable = false)
    private int minJugadores;

    @Min(1)
    @Column(nullable = false)
    private int maxJugadores;

    @Positive
    @Column(nullable = false)
    private int duracionMinutos;

    @OneToMany(mappedBy = "juego", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ejemplar> ejemplares = new ArrayList<>();

    protected Juego() {
    }

    public Juego(String titulo, String categoria, int minJugadores, int maxJugadores, int duracionMinutos) {
        this.titulo = titulo;
        this.categoria = categoria;
        this.minJugadores = minJugadores;
        this.maxJugadores = maxJugadores;
        this.duracionMinutos = duracionMinutos;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getCategoria() {
        return categoria;
    }

    public int getMinJugadores() {
        return minJugadores;
    }

    public int getMaxJugadores() {
        return maxJugadores;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public List<Ejemplar> getEjemplares() {
        return List.copyOf(ejemplares);
    }
}

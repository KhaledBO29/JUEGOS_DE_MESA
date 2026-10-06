package edu.ucc.juegosdemesa.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prestamos")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ejemplar_id", nullable = false)
    private Ejemplar ejemplar;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profesor_responsable_id", nullable = false)
    private Usuario profesorResponsable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_poseedor_actual_id")
    private Usuario estudiantePoseedorActual;

    @Column(nullable = false)
    private LocalDateTime fechaPrestamo;

    private LocalDateTime fechaDevolucion;

    @Column(length = 1000)
    private String observaciones;

    @Column(nullable = false)
    private boolean conNovedad;

    @OneToMany(mappedBy = "prestamo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialPrestamo> historial = new ArrayList<>();

    protected Prestamo() {
    }

    private Prestamo(Ejemplar ejemplar, Usuario profesorResponsable, String observaciones) {
        this.ejemplar = ejemplar;
        this.profesorResponsable = profesorResponsable;
        this.observaciones = observaciones;
        this.fechaPrestamo = LocalDateTime.now();
        this.conNovedad = false;
    }

    public static Prestamo iniciar(Ejemplar ejemplar, Usuario profesorResponsable, String observaciones) {
        return new Prestamo(ejemplar, profesorResponsable, observaciones);
    }

    public void transferirA(Usuario estudiante) {
        this.estudiantePoseedorActual = estudiante;
    }

    public void registrarDevolucion(LocalDateTime fecha, boolean conNovedad, String observaciones) {
        this.fechaDevolucion = fecha;
        this.conNovedad = conNovedad;
        this.observaciones = observaciones;
        this.estudiantePoseedorActual = null;
    }

    public void agregarEvento(HistorialPrestamo evento) {
        historial.add(evento);
        evento.asignarPrestamo(this);
    }

    public boolean estaActivo() {
        return fechaDevolucion == null;
    }

    public Long getId() {
        return id;
    }

    public Ejemplar getEjemplar() {
        return ejemplar;
    }

    public Usuario getProfesorResponsable() {
        return profesorResponsable;
    }

    public Usuario getEstudiantePoseedorActual() {
        return estudiantePoseedorActual;
    }

    public LocalDateTime getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public boolean isConNovedad() {
        return conNovedad;
    }

    public List<HistorialPrestamo> getHistorial() {
        return List.copyOf(historial);
    }
}

package edu.ucc.juegosdemesa.controller.dto;

import edu.ucc.juegosdemesa.model.Prestamo;
import edu.ucc.juegosdemesa.model.EstadoEjemplar;

import java.time.LocalDateTime;
import java.util.List;

public record PrestamoResponse(
        Long id,
        Long ejemplarId,
        String codigoSerie,
        String tituloJuego,
        UsuarioResponse profesorResponsable,
        UsuarioResponse estudiantePoseedorActual,
        LocalDateTime fechaPrestamo,
        LocalDateTime fechaDevolucion,
        String observaciones,
        boolean conNovedad,
        EstadoEjemplar estadoEjemplar,
        boolean activo,
        List<HistorialResponse> historial
) {
    public static PrestamoResponse desde(Prestamo prestamo) {
        return new PrestamoResponse(
                prestamo.getId(),
                prestamo.getEjemplar().getId(),
                prestamo.getEjemplar().getCodigoSerie(),
                prestamo.getEjemplar().getJuego().getTitulo(),
                UsuarioResponse.desde(prestamo.getProfesorResponsable()),
                prestamo.getEstudiantePoseedorActual() == null
                        ? null : UsuarioResponse.desde(prestamo.getEstudiantePoseedorActual()),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucion(),
                prestamo.getObservaciones(),
                prestamo.isConNovedad(),
                prestamo.getEjemplar().getEstado(),
                prestamo.estaActivo(),
                prestamo.getHistorial().stream().map(HistorialResponse::desde).toList()
        );
    }
}

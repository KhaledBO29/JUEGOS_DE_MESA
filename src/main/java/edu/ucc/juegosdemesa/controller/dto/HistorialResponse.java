package edu.ucc.juegosdemesa.controller.dto;

import edu.ucc.juegosdemesa.model.HistorialPrestamo;
import edu.ucc.juegosdemesa.model.TipoEventoPrestamo;

import java.time.LocalDateTime;

public record HistorialResponse(
        Long id,
        TipoEventoPrestamo tipo,
        String carnetActor,
        String detalle,
        LocalDateTime fecha
) {
    public static HistorialResponse desde(HistorialPrestamo evento) {
        return new HistorialResponse(evento.getId(), evento.getTipo(), evento.getCarnetActor(),
                evento.getDetalle(), evento.getFecha());
    }
}

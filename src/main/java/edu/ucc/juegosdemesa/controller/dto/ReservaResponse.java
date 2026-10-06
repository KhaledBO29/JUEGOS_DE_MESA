package edu.ucc.juegosdemesa.controller.dto;

import edu.ucc.juegosdemesa.model.EstadoReserva;
import edu.ucc.juegosdemesa.model.Reserva;

import java.time.LocalDateTime;

public record ReservaResponse(
        Long id,
        String codigoSerie,
        String tituloJuego,
        EstadoReserva estado,
        UsuarioResponse reservante,
        LocalDateTime fechaReserva,
        LocalDateTime fechaExpiracion
) {
    public static ReservaResponse desde(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getEjemplar().getCodigoSerie(),
                reserva.getEjemplar().getJuego().getTitulo(),
                reserva.getEstado(),
                UsuarioResponse.desde(reserva.getUsuario()),
                reserva.getFechaReserva(),
                reserva.getFechaExpiracion()
        );
    }
}

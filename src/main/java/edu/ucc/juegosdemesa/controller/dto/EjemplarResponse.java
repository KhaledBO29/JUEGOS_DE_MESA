package edu.ucc.juegosdemesa.controller.dto;

import edu.ucc.juegosdemesa.model.Ejemplar;
import edu.ucc.juegosdemesa.model.EstadoEjemplar;

import java.util.List;

public record EjemplarResponse(
        Long id,
        String codigoSerie,
        EstadoEjemplar estado,
        Long juegoId,
        String tituloJuego,
        List<ComponenteResponse> componentes
) {
    public static EjemplarResponse desde(Ejemplar ejemplar) {
        return new EjemplarResponse(
                ejemplar.getId(),
                ejemplar.getCodigoSerie(),
                ejemplar.getEstado(),
                ejemplar.getJuego().getId(),
                ejemplar.getJuego().getTitulo(),
                ejemplar.getComponentes().stream().map(ComponenteResponse::desde).toList()
        );
    }
}

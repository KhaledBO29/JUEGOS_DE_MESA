package edu.ucc.juegosdemesa.controller.dto;

import edu.ucc.juegosdemesa.model.Juego;
import edu.ucc.juegosdemesa.model.EstadoEjemplar;

import java.util.List;

public record JuegoResponse(
        Long id,
        String titulo,
        String categoria,
        int minJugadores,
        int maxJugadores,
        int duracionMinutos,
        List<EjemplarResponse> ejemplares,
        long ejemplaresDisponibles,
        long ejemplaresReservados
) {
    public static JuegoResponse desde(Juego juego) {
        var ejemplares = juego.getEjemplares().stream().map(EjemplarResponse::desde).toList();
        return new JuegoResponse(
                juego.getId(), juego.getTitulo(), juego.getCategoria(),
                juego.getMinJugadores(), juego.getMaxJugadores(), juego.getDuracionMinutos(),
                ejemplares,
                ejemplares.stream().filter(e -> e.estado() == EstadoEjemplar.DISPONIBLE).count(),
                ejemplares.stream().filter(e -> e.estado() == EstadoEjemplar.RESERVADO).count()
        );
    }
}

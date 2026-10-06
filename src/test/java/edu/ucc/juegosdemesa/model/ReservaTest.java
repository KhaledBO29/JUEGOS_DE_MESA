package edu.ucc.juegosdemesa.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservaTest {

    @Test
    void reservaActivaVenceEnSuFechaLimite() {
        LocalDateTime creada = LocalDateTime.now();
        Reserva reserva = new Reserva(
                new Ejemplar("TEST-001", EstadoEjemplar.RESERVADO,
                        new Juego("Prueba", "Test", 1, 2, 30)),
                new Usuario("TEST-001", "Usuario prueba", "prueba@example.edu", Rol.ESTUDIANTE),
                creada,
                creada.plusHours(24)
        );

        assertFalse(reserva.estaVencida(creada.plusHours(23)));
        assertTrue(reserva.estaVencida(creada.plusHours(24)));
    }

    @Test
    void reservaConvertidaNoSeConsideraVencida() {
        LocalDateTime creada = LocalDateTime.now();
        Reserva reserva = new Reserva(
                new Ejemplar("TEST-002", EstadoEjemplar.RESERVADO,
                        new Juego("Prueba", "Test", 1, 2, 30)),
                new Usuario("TEST-002", "Usuario prueba", "prueba2@example.edu", Rol.PROFESOR),
                creada,
                creada.plusHours(1)
        );
        reserva.cambiarEstado(EstadoReserva.CONVERTIDA);

        assertFalse(reserva.estaVencida(creada.plusHours(2)));
    }
}

package edu.ucc.juegosdemesa.config;

import edu.ucc.juegosdemesa.repository.JuegoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class DatosInicialesTest {

    @Autowired
    private JuegoRepository juegoRepository;

    @Test
    void cargaCatalogoAmpliadoConComponentes() {
        var juegos = juegoRepository.findAll();

        assertTrue(juegos.size() >= 12);
        assertTrue(juegos.stream().anyMatch(juego -> juego.getTitulo().equals("Pandemic")));
        assertTrue(juegos.stream().anyMatch(juego -> juego.getTitulo().equals("Ticket to Ride")));
        assertTrue(juegos.stream().allMatch(juego ->
                !juego.getEjemplares().isEmpty()
                        && !juego.getEjemplares().getFirst().getComponentes().isEmpty()
        ));
    }
}

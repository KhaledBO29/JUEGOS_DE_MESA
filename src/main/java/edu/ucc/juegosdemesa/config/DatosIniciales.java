package edu.ucc.juegosdemesa.config;

import edu.ucc.juegosdemesa.model.*;
import edu.ucc.juegosdemesa.repository.EjemplarRepository;
import edu.ucc.juegosdemesa.repository.JuegoRepository;
import edu.ucc.juegosdemesa.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DatosIniciales implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final JuegoRepository juegoRepository;
    private final EjemplarRepository ejemplarRepository;

    public DatosIniciales(
            UsuarioRepository usuarioRepository,
            JuegoRepository juegoRepository,
            EjemplarRepository ejemplarRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.juegoRepository = juegoRepository;
        this.ejemplarRepository = ejemplarRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            usuarioRepository.saveAll(List.of(
                    new Usuario("PROF-001", "Dra. Ana Torres", "ana.torres@ucc.edu.co", Rol.PROFESOR),
                    new Usuario("EST-1001", "Santiago Gómez", "santiago.gomez@ucc.edu.co", Rol.ESTUDIANTE),
                    new Usuario("EST-1002", "Valentina Ríos", "valentina.rios@ucc.edu.co", Rol.ESTUDIANTE)
            ));
        }
        crearSiNoExiste("AJD-001", "Ajedrez", "Estrategia", 2, 2, 45,
                new String[][]{{"Rey", "2"}, {"Reina", "2"}, {"Torres", "4"}, {"Alfiles", "4"},
                        {"Caballos", "4"}, {"Peones", "16"}, {"Tablero", "1"}});
        crearSiNoExiste("CAT-001", "Catan", "Estrategia", 3, 4, 90,
                new String[][]{{"Losetas de terreno", "19"}, {"Cartas de recurso", "95"},
                        {"Carreteras", "60"}, {"Poblados", "20"}, {"Ciudades", "16"}, {"Dados", "2"}});
        crearSiNoExiste("MON-001", "Monopoly", "Familiar", 2, 6, 120,
                new String[][]{{"Tablero", "1"}, {"Peones", "8"}, {"Dados", "2"},
                        {"Casas", "32"}, {"Hoteles", "12"}, {"Billetes", "1"}});
        crearSiNoExiste("SCR-001", "Scrabble", "Palabras", 2, 4, 60,
                new String[][]{{"Tablero", "1"}, {"Fichas de letras", "100"}, {"Atriles", "4"}});
        crearSiNoExiste("TRK-001", "Ticket to Ride", "Aventura", 2, 5, 60,
                new String[][]{{"Tablero de rutas", "1"}, {"Trenes", "240"}, {"Cartas de vagón", "110"}, {"Cartas de destino", "46"}});
        crearSiNoExiste("PAN-001", "Pandemic", "Cooperativo", 2, 4, 45,
                new String[][]{{"Tablero", "1"}, {"Peones", "7"}, {"Cubos de enfermedad", "96"}, {"Cartas de jugador", "59"}, {"Cartas de infección", "48"}});
        crearSiNoExiste("DIX-001", "Dixit", "Creatividad", 3, 6, 30,
                new String[][]{{"Tablero de puntuación", "1"}, {"Cartas ilustradas", "84"}, {"Peones", "6"}, {"Discos de votación", "36"}});
        crearSiNoExiste("UNO-001", "UNO", "Cartas", 2, 10, 30,
                new String[][]{{"Cartas UNO", "112"}, {"Instructivo", "1"}});
        crearSiNoExiste("CAR-001", "Carcassonne", "Estrategia", 2, 5, 45,
                new String[][]{{"Losetas de terreno", "72"}, {"Seguidores", "40"}, {"Tablero de puntuación", "1"}});
        crearSiNoExiste("RKS-001", "Risk", "Estrategia", 2, 5, 120,
                new String[][]{{"Tablero", "1"}, {"Ejércitos", "300"}, {"Cartas de territorio", "56"}, {"Dados", "5"}});
        crearSiNoExiste("CLU-001", "Clue", "Misterio", 2, 6, 45,
                new String[][]{{"Tablero", "1"}, {"Peones", "6"}, {"Cartas de sospechoso", "21"}, {"Dados", "2"}});
        crearSiNoExiste("AZU-001", "Azul", "Abstracto", 2, 4, 45,
                new String[][]{{"Azulejos", "100"}, {"Tableros de jugador", "4"}, {"Fichas de fábrica", "9"}, {"Marcador inicial", "1"}});
    }

    private void crearSiNoExiste(
            String codigo, String titulo, String categoria,
            int minJugadores, int maxJugadores, int duracion, String[][] componentes
    ) {
        if (!juegoRepository.existsByTituloIgnoreCase(titulo)) {
            crearEjemplar(codigo, titulo, categoria, minJugadores, maxJugadores, duracion, componentes);
        }
    }

    private void crearEjemplar(
            String codigo,
            String titulo,
            String categoria,
            int minJugadores,
            int maxJugadores,
            int duracion,
            String[][] componentes
    ) {
        Juego juego = juegoRepository.save(
                new Juego(titulo, categoria, minJugadores, maxJugadores, duracion)
        );
        Ejemplar ejemplar = new Ejemplar(codigo, EstadoEjemplar.DISPONIBLE, juego);
        for (String[] componente : componentes) {
            ejemplar.agregarComponente(new Componente(componente[0], Integer.parseInt(componente[1])));
        }
        ejemplarRepository.save(ejemplar);
    }
}

package edu.ucc.juegosdemesa.factory;

import edu.ucc.juegosdemesa.model.EstadoEjemplar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EvaluadorEstadoFactoryTest {

    private final EvaluadorEstadoFactory factory = new EvaluadorEstadoFactory();

    @Test
    void ejemplarCompletoYEnBuenEstadoQuedaDisponible() {
        assertEquals(EstadoEjemplar.DISPONIBLE, factory.evaluar(false, false));
    }

    @Test
    void faltantesQuedanRegistradosComoNovedad() {
        assertEquals(EstadoEjemplar.DEVUELTO_CON_NOVEDAD, factory.evaluar(true, false));
    }

    @Test
    void danosPriorizanElMantenimientoAunqueTambienFaltenPiezas() {
        assertEquals(EstadoEjemplar.MANTENIMIENTO, factory.evaluar(true, true));
    }
}

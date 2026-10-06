package edu.ucc.juegosdemesa.factory;

import edu.ucc.juegosdemesa.model.EstadoEjemplar;
import org.springframework.stereotype.Component;

@Component
public class EvaluadorEstadoFactory {

    public EstadoEjemplar evaluar(boolean hayPiezasFaltantes, boolean hayDanos) {
        if (hayDanos) {
            return EstadoEjemplar.MANTENIMIENTO;
        }
        if (hayPiezasFaltantes) {
            return EstadoEjemplar.DEVUELTO_CON_NOVEDAD;
        }
        return EstadoEjemplar.DISPONIBLE;
    }
}

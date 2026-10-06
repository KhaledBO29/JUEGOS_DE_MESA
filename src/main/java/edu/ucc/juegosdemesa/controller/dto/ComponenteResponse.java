package edu.ucc.juegosdemesa.controller.dto;

import edu.ucc.juegosdemesa.model.Componente;

public record ComponenteResponse(Long id, String nombre, int cantidad) {
    public static ComponenteResponse desde(Componente componente) {
        return new ComponenteResponse(componente.getId(), componente.getNombre(), componente.getCantidad());
    }
}

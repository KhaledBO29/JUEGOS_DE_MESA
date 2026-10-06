package edu.ucc.juegosdemesa.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record PrestamoRequest(
        @NotBlank String codigoSerie,
        @NotBlank String profesorCarnet,
        Long reservaId,
        String observaciones
) {
}

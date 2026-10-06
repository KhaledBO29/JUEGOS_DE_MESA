package edu.ucc.juegosdemesa.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record DevolucionRequest(
        @NotBlank String carnetActor,
        boolean piezasFaltantes,
        boolean danos,
        String observaciones
) {
}

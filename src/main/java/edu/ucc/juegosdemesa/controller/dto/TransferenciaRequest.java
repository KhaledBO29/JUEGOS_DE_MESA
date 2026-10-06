package edu.ucc.juegosdemesa.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record TransferenciaRequest(
        @NotBlank String profesorCarnet,
        @NotBlank String estudianteCarnet
) {
}

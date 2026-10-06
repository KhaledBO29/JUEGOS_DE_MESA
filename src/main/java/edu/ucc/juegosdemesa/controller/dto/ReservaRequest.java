package edu.ucc.juegosdemesa.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record ReservaRequest(
        @NotBlank String codigoSerie,
        @NotBlank String carnet
) {
}

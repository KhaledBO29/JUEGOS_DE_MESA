package edu.ucc.juegosdemesa.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelarReservaRequest(@NotBlank String carnet) {
}

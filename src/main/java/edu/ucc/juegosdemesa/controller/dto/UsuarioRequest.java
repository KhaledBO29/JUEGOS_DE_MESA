package edu.ucc.juegosdemesa.controller.dto;

import edu.ucc.juegosdemesa.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRequest(
        @NotBlank String carnet,
        @NotBlank String nombre,
        @NotBlank @Email String email,
        @NotNull Rol rol
) {
}

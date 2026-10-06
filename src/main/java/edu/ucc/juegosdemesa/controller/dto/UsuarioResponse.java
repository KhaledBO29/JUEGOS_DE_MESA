package edu.ucc.juegosdemesa.controller.dto;

import edu.ucc.juegosdemesa.model.Rol;
import edu.ucc.juegosdemesa.model.Usuario;

public record UsuarioResponse(Long id, String carnet, String nombre, String email, Rol rol) {
    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getCarnet(), usuario.getNombre(),
                usuario.getEmail(), usuario.getRol());
    }
}

package edu.ucc.juegosdemesa.repository;

import edu.ucc.juegosdemesa.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCarnet(String carnet);

    boolean existsByCarnet(String carnet);

    boolean existsByEmail(String email);
}

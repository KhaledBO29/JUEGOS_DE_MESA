package edu.ucc.juegosdemesa.repository;

import edu.ucc.juegosdemesa.model.Juego;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JuegoRepository extends JpaRepository<Juego, Long> {
    boolean existsByTituloIgnoreCase(String titulo);
}

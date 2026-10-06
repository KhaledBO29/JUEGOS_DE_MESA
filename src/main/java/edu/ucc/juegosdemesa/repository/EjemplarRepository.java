package edu.ucc.juegosdemesa.repository;

import edu.ucc.juegosdemesa.model.Ejemplar;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EjemplarRepository extends JpaRepository<Ejemplar, Long> {
    Optional<Ejemplar> findByCodigoSerie(String codigoSerie);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Ejemplar e where e.id = :id")
    Optional<Ejemplar> findByIdForUpdate(@Param("id") Long id);
}

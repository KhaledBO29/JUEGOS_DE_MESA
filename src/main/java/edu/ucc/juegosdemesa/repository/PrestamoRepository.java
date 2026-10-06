package edu.ucc.juegosdemesa.repository;

import edu.ucc.juegosdemesa.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    Optional<Prestamo> findByEjemplarIdAndFechaDevolucionIsNull(Long ejemplarId);

    List<Prestamo> findAllByFechaDevolucionIsNullOrderByFechaPrestamoDesc();

    List<Prestamo> findAllByOrderByFechaPrestamoDesc();
}

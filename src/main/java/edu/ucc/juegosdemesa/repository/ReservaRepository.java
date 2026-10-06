package edu.ucc.juegosdemesa.repository;

import edu.ucc.juegosdemesa.model.EstadoReserva;
import edu.ucc.juegosdemesa.model.Reserva;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findAllByEstadoAndFechaExpiracionLessThanEqual(
            EstadoReserva estado,
            LocalDateTime fechaExpiracion
    );

    List<Reserva> findAllByEstadoOrderByFechaReservaAsc(EstadoReserva estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Reserva r where r.id = :id")
    Optional<Reserva> findByIdForUpdate(@Param("id") Long id);
}

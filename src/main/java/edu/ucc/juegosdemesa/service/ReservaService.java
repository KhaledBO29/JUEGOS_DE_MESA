package edu.ucc.juegosdemesa.service;

import edu.ucc.juegosdemesa.controller.dto.CancelarReservaRequest;
import edu.ucc.juegosdemesa.controller.dto.ReservaRequest;
import edu.ucc.juegosdemesa.controller.dto.ReservaResponse;
import edu.ucc.juegosdemesa.model.Ejemplar;
import edu.ucc.juegosdemesa.model.EstadoEjemplar;
import edu.ucc.juegosdemesa.model.EstadoReserva;
import edu.ucc.juegosdemesa.model.Reserva;
import edu.ucc.juegosdemesa.model.Usuario;
import edu.ucc.juegosdemesa.repository.EjemplarRepository;
import edu.ucc.juegosdemesa.repository.ReservaRepository;
import edu.ucc.juegosdemesa.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class ReservaService {

    private final UsuarioRepository usuarioRepository;
    private final EjemplarRepository ejemplarRepository;
    private final ReservaRepository reservaRepository;
    private final long duracionHoras;

    public ReservaService(
            UsuarioRepository usuarioRepository,
            EjemplarRepository ejemplarRepository,
            ReservaRepository reservaRepository,
            @Value("${app.reservas.duracion-horas:24}") long duracionHoras
    ) {
        this.usuarioRepository = usuarioRepository;
        this.ejemplarRepository = ejemplarRepository;
        this.reservaRepository = reservaRepository;
        this.duracionHoras = duracionHoras;
    }

    public ReservaResponse crear(ReservaRequest request) {
        validarDuracion();
        expirarReservasVencidas();
        String codigoSerie = request.codigoSerie().trim();
        Ejemplar encontrado = ejemplarRepository.findByCodigoSerie(codigoSerie)
                .orElseThrow(() -> new IllegalArgumentException("No existe el ejemplar " + codigoSerie + "."));
        Ejemplar ejemplar = ejemplarRepository.findByIdForUpdate(encontrado.getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe el ejemplar " + codigoSerie + "."));
        if (ejemplar.getEstado() != EstadoEjemplar.DISPONIBLE) {
            throw new IllegalStateException("Solo se pueden reservar ejemplares disponibles.");
        }
        Usuario usuario = usuarioRepository.findByCarnet(normalizarCarnet(request.carnet()))
                .orElseThrow(() -> new IllegalArgumentException("No existe un usuario registrado con ese carnet."));

        LocalDateTime ahora = LocalDateTime.now();
        ejemplar.actualizarEstado(EstadoEjemplar.RESERVADO);
        Reserva reserva = new Reserva(ejemplar, usuario, ahora, ahora.plusHours(duracionHoras));
        return ReservaResponse.desde(reservaRepository.save(reserva));
    }

    public ReservaResponse cancelar(Long reservaId, CancelarReservaRequest request) {
        Reserva encontrada = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("No existe la reserva " + reservaId + "."));
        ejemplarRepository.findByIdForUpdate(encontrada.getEjemplar().getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe el ejemplar de la reserva."));
        Reserva reserva = reservaRepository.findByIdForUpdate(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("No existe la reserva " + reservaId + "."));
        if (reserva.getEstado() != EstadoReserva.ACTIVA) {
            throw new IllegalStateException("La reserva ya no está activa.");
        }
        if (reserva.estaVencida(LocalDateTime.now())) {
            finalizarReserva(reserva, EstadoReserva.VENCIDA);
            return ReservaResponse.desde(reserva);
        }
        String carnet = normalizarCarnet(request.carnet());
        if (!reserva.getUsuario().getCarnet().equals(carnet)) {
            throw new IllegalStateException("Solo el titular de la reserva puede cancelarla.");
        }
        finalizarReserva(reserva, EstadoReserva.CANCELADA);
        return ReservaResponse.desde(reserva);
    }

    public List<ReservaResponse> listar(boolean activas) {
        expirarReservasVencidas();
        List<Reserva> reservas = activas
                ? reservaRepository.findAllByEstadoOrderByFechaReservaAsc(EstadoReserva.ACTIVA)
                : reservaRepository.findAll();
        return reservas.stream().map(ReservaResponse::desde).toList();
    }

    private void expirarReservasVencidas() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Reserva> vencidas = reservaRepository.findAllByEstadoAndFechaExpiracionLessThanEqual(
                EstadoReserva.ACTIVA, ahora
        );
        for (Reserva reserva : vencidas) {
            Ejemplar ejemplar = ejemplarRepository.findByIdForUpdate(reserva.getEjemplar().getId())
                    .orElseThrow(() -> new IllegalStateException("No existe el ejemplar de la reserva vencida."));
            if (reserva.getEstado() == EstadoReserva.ACTIVA && reserva.estaVencida(ahora)) {
                finalizarReserva(reserva, EstadoReserva.VENCIDA);
            }
            if (ejemplar.getEstado() == EstadoEjemplar.RESERVADO) {
                ejemplar.actualizarEstado(EstadoEjemplar.DISPONIBLE);
            }
        }
    }

    private void finalizarReserva(Reserva reserva, EstadoReserva estado) {
        reserva.cambiarEstado(estado);
        if (reserva.getEjemplar().getEstado() == EstadoEjemplar.RESERVADO) {
            reserva.getEjemplar().actualizarEstado(EstadoEjemplar.DISPONIBLE);
        }
    }

    private void validarDuracion() {
        if (duracionHoras < 1) {
            throw new IllegalStateException("La duración configurada para reservas debe ser de al menos una hora.");
        }
    }

    private String normalizarCarnet(String carnet) {
        return carnet.trim().toUpperCase(Locale.ROOT);
    }
}

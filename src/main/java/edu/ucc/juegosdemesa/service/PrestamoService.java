package edu.ucc.juegosdemesa.service;

import edu.ucc.juegosdemesa.controller.dto.*;
import edu.ucc.juegosdemesa.factory.EvaluadorEstadoFactory;
import edu.ucc.juegosdemesa.model.*;
import edu.ucc.juegosdemesa.repository.EjemplarRepository;
import edu.ucc.juegosdemesa.repository.JuegoRepository;
import edu.ucc.juegosdemesa.repository.PrestamoRepository;
import edu.ucc.juegosdemesa.repository.ReservaRepository;
import edu.ucc.juegosdemesa.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class PrestamoService {

    private final UsuarioRepository usuarioRepository;
    private final JuegoRepository juegoRepository;
    private final EjemplarRepository ejemplarRepository;
    private final PrestamoRepository prestamoRepository;
    private final ReservaRepository reservaRepository;
    private final EvaluadorEstadoFactory evaluadorEstadoFactory;

    public PrestamoService(
            UsuarioRepository usuarioRepository,
            JuegoRepository juegoRepository,
            EjemplarRepository ejemplarRepository,
            PrestamoRepository prestamoRepository,
            ReservaRepository reservaRepository,
            EvaluadorEstadoFactory evaluadorEstadoFactory
    ) {
        this.usuarioRepository = usuarioRepository;
        this.juegoRepository = juegoRepository;
        this.ejemplarRepository = ejemplarRepository;
        this.prestamoRepository = prestamoRepository;
        this.reservaRepository = reservaRepository;
        this.evaluadorEstadoFactory = evaluadorEstadoFactory;
    }

    @Transactional(readOnly = true)
    public List<JuegoResponse> listarJuegos() {
        return juegoRepository.findAll().stream().map(JuegoResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public List<EjemplarResponse> listarEjemplares() {
        return ejemplarRepository.findAll().stream().map(EjemplarResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarUsuarioPorCarnet(String carnet) {
        return UsuarioResponse.desde(obtenerUsuario(carnet));
    }

    public UsuarioResponse registrarUsuario(UsuarioRequest request) {
        String carnet = normalizarCarnet(request.carnet());
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (usuarioRepository.existsByCarnet(carnet)) {
            throw new IllegalArgumentException("Ya existe un usuario con ese carnet.");
        }
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo electrónico.");
        }
        Usuario usuario = new Usuario(carnet, request.nombre().trim(), email, request.rol());
        return UsuarioResponse.desde(usuarioRepository.save(usuario));
    }

    public PrestamoResponse registrarPrestamo(PrestamoRequest request) {
        String codigoSerie = request.codigoSerie().trim();
        Ejemplar ejemplar = ejemplarRepository.findByCodigoSerie(codigoSerie)
                .orElseThrow(() -> new IllegalArgumentException("No existe el ejemplar " + codigoSerie + "."));
        ejemplar = ejemplarRepository.findByIdForUpdate(ejemplar.getId())
                .orElseThrow(() -> new IllegalArgumentException("El ejemplar ya no está disponible."));
        Reserva reserva = null;
        if (ejemplar.getEstado() == EstadoEjemplar.RESERVADO && request.reservaId() != null) {
            reserva = reservaRepository.findByIdForUpdate(request.reservaId())
                    .orElseThrow(() -> new IllegalArgumentException("No existe la reserva indicada."));
            if (!reserva.getEjemplar().getId().equals(ejemplar.getId())
                    || reserva.getEstado() != EstadoReserva.ACTIVA
                    || reserva.estaVencida(LocalDateTime.now())) {
                throw new IllegalStateException("La reserva no está activa para este ejemplar.");
            }
        } else if (ejemplar.getEstado() != EstadoEjemplar.DISPONIBLE || request.reservaId() != null) {
            throw new IllegalStateException("El ejemplar no está disponible para préstamo.");
        }

        Usuario profesor = obtenerUsuario(request.profesorCarnet());
        exigirRol(profesor, Rol.PROFESOR, "Solo un profesor puede quedar como responsable del préstamo.");

        if (reserva != null) {
            reserva.cambiarEstado(EstadoReserva.CONVERTIDA);
        }
        Prestamo prestamo = Prestamo.iniciar(ejemplar, profesor, limpiar(request.observaciones()));
        prestamo.agregarEvento(new HistorialPrestamo(
                TipoEventoPrestamo.PRESTAMO_REGISTRADO,
                profesor.getCarnet(),
                "Préstamo inicial al profesor responsable."
        ));
        ejemplar.actualizarEstado(EstadoEjemplar.PRESTADO);
        return PrestamoResponse.desde(prestamoRepository.save(prestamo));
    }

    public PrestamoResponse transferirPosesion(Long prestamoId, TransferenciaRequest request) {
        Prestamo prestamo = obtenerPrestamoActivoForUpdate(prestamoId);
        Usuario profesor = obtenerUsuario(request.profesorCarnet());
        exigirRol(profesor, Rol.PROFESOR, "La transferencia debe ser autorizada por un profesor.");
        if (!prestamo.getProfesorResponsable().getId().equals(profesor.getId())) {
            throw new IllegalStateException("Solo el profesor responsable puede transferir este préstamo.");
        }
        Usuario estudiante = obtenerUsuario(request.estudianteCarnet());
        exigirRol(estudiante, Rol.ESTUDIANTE, "El poseedor actual debe ser un estudiante.");

        String poseedorAnterior = prestamo.getEstudiantePoseedorActual() == null
                ? prestamo.getProfesorResponsable().getCarnet()
                : prestamo.getEstudiantePoseedorActual().getCarnet();
        prestamo.transferirA(estudiante);
        prestamo.agregarEvento(new HistorialPrestamo(
                TipoEventoPrestamo.TRANSFERENCIA_POSESION,
                profesor.getCarnet(),
                "Posesión transferida de " + poseedorAnterior + " a " + estudiante.getCarnet() + "."
        ));
        return PrestamoResponse.desde(prestamo);
    }

    public PrestamoResponse registrarDevolucion(Long prestamoId, DevolucionRequest request) {
        Prestamo prestamo = obtenerPrestamoActivoForUpdate(prestamoId);
        Usuario actor = obtenerUsuario(request.carnetActor());
        validarResponsableDevolucion(prestamo, actor);

        EstadoEjemplar nuevoEstado = evaluadorEstadoFactory.evaluar(
                request.piezasFaltantes(), request.danos()
        );
        String observacionesDevolucion = componerObservaciones(request);
        String observaciones = combinarObservaciones(prestamo.getObservaciones(), observacionesDevolucion);
        prestamo.registrarDevolucion(LocalDateTime.now(),
                request.piezasFaltantes() || request.danos(), observaciones);
        prestamo.getEjemplar().actualizarEstado(nuevoEstado);
        prestamo.agregarEvento(new HistorialPrestamo(
                TipoEventoPrestamo.DEVOLUCION_REGISTRADA,
                actor.getCarnet(),
                "Estado del ejemplar: " + nuevoEstado + ". " + observacionesDevolucion
        ));
        return PrestamoResponse.desde(prestamo);
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponse> listarPrestamos(boolean activos) {
        List<Prestamo> prestamos = activos
                ? prestamoRepository.findAllByFechaDevolucionIsNullOrderByFechaPrestamoDesc()
                : prestamoRepository.findAllByOrderByFechaPrestamoDesc();
        return prestamos.stream().map(PrestamoResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public PrestamoResponse obtenerPrestamo(Long prestamoId) {
        return PrestamoResponse.desde(prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new IllegalArgumentException("No existe el préstamo " + prestamoId + ".")));
    }

    private Usuario obtenerUsuario(String carnet) {
        return usuarioRepository.findByCarnet(normalizarCarnet(carnet))
                .orElseThrow(() -> new IllegalArgumentException("No existe un usuario registrado con ese carnet."));
    }

    private Prestamo obtenerPrestamoActivoForUpdate(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el préstamo " + id + "."));
        Ejemplar ejemplarBloqueado = ejemplarRepository.findByIdForUpdate(prestamo.getEjemplar().getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe el ejemplar del préstamo."));
        return prestamoRepository.findByEjemplarIdAndFechaDevolucionIsNull(ejemplarBloqueado.getId())
                .orElseThrow(() -> new IllegalStateException("El préstamo ya fue cerrado."));
    }

    private void exigirRol(Usuario usuario, Rol esperado, String mensaje) {
        if (usuario.getRol() != esperado) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    private void validarResponsableDevolucion(Prestamo prestamo, Usuario actor) {
        boolean esProfesorResponsable = prestamo.getProfesorResponsable().getId().equals(actor.getId());
        boolean esPoseedorActual = prestamo.getEstudiantePoseedorActual() != null
                && prestamo.getEstudiantePoseedorActual().getId().equals(actor.getId());
        if (!esProfesorResponsable && !esPoseedorActual) {
            throw new IllegalStateException("Solo el profesor responsable o el estudiante poseedor pueden registrar la devolución.");
        }
    }

    private String normalizarCarnet(String carnet) {
        return carnet.trim().toUpperCase(Locale.ROOT);
    }

    private String limpiar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private String componerObservaciones(DevolucionRequest request) {
        String texto = limpiar(request.observaciones());
        if (!request.piezasFaltantes() && !request.danos()) {
            return texto;
        }
        String incidencias = String.join(", ",
                request.piezasFaltantes() ? "piezas faltantes" : "",
                request.danos() ? "daños reportados" : "").replaceAll("(^, |, $)", "");
        return texto == null ? incidencias : incidencias + ": " + texto;
    }

    private String combinarObservaciones(String iniciales, String devolucion) {
        if (iniciales == null) {
            return devolucion;
        }
        if (devolucion == null) {
            return iniciales;
        }
        return "Préstamo: " + iniciales + " | Devolución: " + devolucion;
    }
}

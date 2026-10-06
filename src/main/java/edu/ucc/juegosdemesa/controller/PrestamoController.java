package edu.ucc.juegosdemesa.controller;

import edu.ucc.juegosdemesa.controller.dto.*;
import edu.ucc.juegosdemesa.service.PrestamoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @GetMapping("/juegos")
    public List<JuegoResponse> listarJuegos() {
        return prestamoService.listarJuegos();
    }

    @GetMapping("/ejemplares")
    public List<EjemplarResponse> listarEjemplares() {
        return prestamoService.listarEjemplares();
    }

    @GetMapping("/usuarios/carnet/{carnet}")
    public UsuarioResponse buscarUsuario(@PathVariable String carnet) {
        return prestamoService.buscarUsuarioPorCarnet(carnet);
    }

    @PostMapping("/usuarios")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrarUsuario(@Valid @RequestBody UsuarioRequest request) {
        return prestamoService.registrarUsuario(request);
    }

    @PostMapping("/prestamos")
    @ResponseStatus(HttpStatus.CREATED)
    public PrestamoResponse registrarPrestamo(@Valid @RequestBody PrestamoRequest request) {
        return prestamoService.registrarPrestamo(request);
    }

    @PatchMapping("/prestamos/{id}/transferencias")
    public PrestamoResponse transferirPosesion(
            @PathVariable Long id,
            @Valid @RequestBody TransferenciaRequest request
    ) {
        return prestamoService.transferirPosesion(id, request);
    }

    @PostMapping("/prestamos/{id}/devolucion")
    public PrestamoResponse registrarDevolucion(
            @PathVariable Long id,
            @Valid @RequestBody DevolucionRequest request
    ) {
        return prestamoService.registrarDevolucion(id, request);
    }

    @GetMapping("/prestamos")
    public List<PrestamoResponse> listarPrestamos(@RequestParam(defaultValue = "false") boolean activos) {
        return prestamoService.listarPrestamos(activos);
    }

    @GetMapping("/prestamos/{id}")
    public PrestamoResponse obtenerPrestamo(@PathVariable Long id) {
        return prestamoService.obtenerPrestamo(id);
    }
}

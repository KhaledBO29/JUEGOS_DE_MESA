package edu.ucc.juegosdemesa.controller;

import edu.ucc.juegosdemesa.controller.dto.CancelarReservaRequest;
import edu.ucc.juegosdemesa.controller.dto.ReservaRequest;
import edu.ucc.juegosdemesa.controller.dto.ReservaResponse;
import edu.ucc.juegosdemesa.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaResponse crear(@Valid @RequestBody ReservaRequest request) {
        return reservaService.crear(request);
    }

    @PostMapping("/{id}/cancelacion")
    public ReservaResponse cancelar(
            @PathVariable Long id,
            @Valid @RequestBody CancelarReservaRequest request
    ) {
        return reservaService.cancelar(id, request);
    }

    @GetMapping
    public List<ReservaResponse> listar(@RequestParam(defaultValue = "true") boolean activas) {
        return reservaService.listar(activas);
    }
}

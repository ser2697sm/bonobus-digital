package io.github.ser2697sm.bonos.bono;

import io.github.ser2697sm.bonos.bono.dto.BonoResponse;
import io.github.ser2697sm.bonos.bono.dto.CrearBonoRequest;
import io.github.ser2697sm.bonos.bono.dto.MovimientoResponse;
import io.github.ser2697sm.bonos.bono.dto.RecargaRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/bonos")
@RequiredArgsConstructor
public class BonoController {

    private final BonoService bonoService;

    /** Crea un bono nuevo con 0 viajes. Devuelve 201 + cabecera Location. */
    @PostMapping
    public ResponseEntity<BonoResponse> crear(@Valid @RequestBody CrearBonoRequest request) {
        Bono bono = bonoService.crearBono(request.titular());

        // Construye la URL del bono recién creado para la cabecera "Location" de la respuesta:
        //   fromCurrentRequest()          -> http://localhost:8080/api/bonos   (URL de esta petición)
        //   .path("/{codigo}")            -> http://localhost:8080/api/bonos/{codigo}
        //   .buildAndExpand(codigo)       -> http://localhost:8080/api/bonos/LOR-3F9A1C2B
        //   .toUri()                      -> la convierte en un objeto URI
        // Es el estándar HTTP al crear un recurso (201 Created + Location): el cliente sabe dónde consultarlo.
        // Se basa en la petición real, así que funciona igual en otro dominio o puerto.
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{codigo}")
                .buildAndExpand(bono.getCodigo())
                .toUri();
        return ResponseEntity.created(location).body(BonoResponse.from(bono));
    }

    /** Consulta un bono: viajes restantes, si está agotado... */
    @GetMapping("/{codigo}")
    public BonoResponse obtener(@PathVariable String codigo) {
        return BonoResponse.from(bonoService.obtenerBono(codigo));
    }

    /** Recarga viajes. Devuelve el bono actualizado. */
    @PostMapping("/{codigo}/recargas")
    public BonoResponse recargar(@PathVariable String codigo,
                                 @Valid @RequestBody RecargaRequest request) {
        Movimiento movimiento = bonoService.recargar(codigo, request.viajes());
        return BonoResponse.from(movimiento.getBono());
    }

    /** Simula pasar el bono por la máquina del autobús. Devuelve el bono actualizado. */
    @PostMapping("/{codigo}/viajes")
    public BonoResponse registrarViaje(@PathVariable String codigo) {
        Movimiento movimiento = bonoService.registrarViaje(codigo);
        return BonoResponse.from(movimiento.getBono());
    }

    /** Historial de viajes y recargas, paginado: ?page=0&size=20 */
    @GetMapping("/{codigo}/movimientos")
    public PagedModel<MovimientoResponse> historial(@PathVariable String codigo,
                                                    @PageableDefault(size = 20) Pageable pageable) {
        return new PagedModel<>(bonoService.historial(codigo, pageable).map(MovimientoResponse::from));
    }
}

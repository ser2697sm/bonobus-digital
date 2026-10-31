package io.github.ser2697sm.bonos.bono.dto;

import io.github.ser2697sm.bonos.bono.Movimiento;
import io.github.ser2697sm.bonos.bono.TipoMovimiento;

import java.time.Instant;

public record MovimientoResponse(
        TipoMovimiento tipo,
        int viajes,
        Instant fecha
) {
    public static MovimientoResponse from(Movimiento movimiento) {
        return new MovimientoResponse(
                movimiento.getTipo(),
                movimiento.getViajes(),
                movimiento.getFecha()
        );
    }
}

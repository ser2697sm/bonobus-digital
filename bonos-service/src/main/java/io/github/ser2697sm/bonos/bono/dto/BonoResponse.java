package io.github.ser2697sm.bonos.bono.dto;

import io.github.ser2697sm.bonos.bono.Bono;

import java.time.Instant;

public record BonoResponse(
        String codigo,
        String titular,
        int viajesRestantes,
        boolean agotado,
        Instant fechaCreacion
) {
    public static BonoResponse from(Bono bono) {
        return new BonoResponse(
                bono.getCodigo(),
                bono.getTitular(),
                bono.getViajesRestantes(),
                bono.estaAgotado(),
                bono.getFechaCreacion()
        );
    }
}

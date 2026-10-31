package io.github.ser2697sm.bonos.bono.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RecargaRequest(
        @NotNull(message = "Indica cuántos viajes quieres recargar")
        @Positive(message = "El número de viajes debe ser mayor que 0")
        @Max(value = 100, message = "No se pueden recargar más de 100 viajes de una vez")
        Integer viajes
) {
}

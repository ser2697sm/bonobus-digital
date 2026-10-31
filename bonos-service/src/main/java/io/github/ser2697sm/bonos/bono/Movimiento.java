package io.github.ser2697sm.bonos.bono;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Cada cosa que le pasa a un bono: un viaje (-1) o una recarga (+N).
 * El historial del bono es la lista de sus movimientos.
 */
@Entity
@Table(name = "movimiento")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // muchos movimientos -> un bono
    @JoinColumn(name = "bono_id", nullable = false)      // columna con la clave foránea
    private Bono bono;

    @Enumerated(EnumType.STRING) // guarda "VIAJE"/"RECARGA", no 0/1
    @Column(nullable = false, length = 10)
    private TipoMovimiento tipo;

    @Column(nullable = false)
    private int viajes; // -1 en un viaje, +N en una recarga

    @Column(nullable = false, updatable = false)
    private Instant fecha;

    private Movimiento(Bono bono, TipoMovimiento tipo, int viajes) {
        this.bono = bono;
        this.tipo = tipo;
        this.viajes = viajes;
        this.fecha = Instant.now();
    }

    static Movimiento recarga(Bono bono, int viajes) {
        return new Movimiento(bono, TipoMovimiento.RECARGA, viajes);
    }

    static Movimiento viaje(Bono bono) {
        return new Movimiento(bono, TipoMovimiento.VIAJE, -1);
    }
}

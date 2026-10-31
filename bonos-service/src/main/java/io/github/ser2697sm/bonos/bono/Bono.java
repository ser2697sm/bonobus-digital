package io.github.ser2697sm.bonos.bono;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Un bono de viajes. Nace con 0 viajes, se recarga sumando viajes
 * y cada viaje descuenta uno. Con 0 viajes no se puede viajar.
 */
@Entity
@Table(name = "bono")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA lo necesita; protected para que tú no lo uses
public class Bono {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PostgreSQL genera el id (1, 2, 3...)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo; // identificador "público", ej. LOR-0001

    @Column(nullable = false, length = 100)
    private String titular;

    @Column(name = "viajes_restantes", nullable = false)
    private int viajesRestantes;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private Instant fechaCreacion;

    @Version // control de concurrencia: evita que dos viajes simultáneos descuenten mal
    private long version;

    public Bono(String codigo, String titular) {
        this.codigo = codigo;
        this.titular = titular;
        this.viajesRestantes = 0;
        this.fechaCreacion = Instant.now();
    }

    /** Suma viajes al bono y devuelve el movimiento para guardarlo en el historial. */
    public Movimiento recargar(int viajes) {
        if (viajes <= 0) {
            throw new IllegalArgumentException("El número de viajes a recargar debe ser mayor que 0");
        }
        this.viajesRestantes += viajes;
        return Movimiento.recarga(this, viajes);
    }

    /** Descuenta un viaje. Si no quedan viajes, lanza BonoSinViajesException. */
    public Movimiento registrarViaje() {
        if (estaAgotado()) {
            throw new BonoSinViajesException(codigo);
        }
        this.viajesRestantes--;
        return Movimiento.viaje(this);
    }

    public boolean estaAgotado() {
        return viajesRestantes == 0;
    }
}

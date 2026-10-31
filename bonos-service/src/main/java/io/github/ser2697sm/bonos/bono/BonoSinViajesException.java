package io.github.ser2697sm.bonos.bono;

/** Se lanza al intentar viajar con un bono que tiene 0 viajes. */
public class BonoSinViajesException extends RuntimeException {

    public BonoSinViajesException(String codigo) {
        super("El bono " + codigo + " no tiene viajes. Recárgalo para seguir viajando.");
    }
}

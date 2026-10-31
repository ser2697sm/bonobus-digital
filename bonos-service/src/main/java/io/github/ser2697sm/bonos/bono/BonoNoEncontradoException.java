package io.github.ser2697sm.bonos.bono;

/** Se lanza cuando se busca un bono por un código que no existe. */
public class BonoNoEncontradoException extends RuntimeException {

    public BonoNoEncontradoException(String codigo) {
        super("No existe ningún bono con código " + codigo);
    }
}

package io.github.ser2697sm.bonos.common.error;

import io.github.ser2697sm.bonos.bono.BonoNoEncontradoException;
import io.github.ser2697sm.bonos.bono.BonoSinViajesException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Convierte las excepciones en respuestas HTTP con formato ProblemDetail (RFC 9457).
 * Al extender ResponseEntityExceptionHandler, los errores de validación (@Valid)
 * también salen con este formato.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BonoNoEncontradoException.class)
    public ProblemDetail bonoNoEncontrado(BonoNoEncontradoException ex) {
        return problema(HttpStatus.NOT_FOUND, "Bono no encontrado", ex.getMessage());
    }

    @ExceptionHandler(BonoSinViajesException.class)
    public ProblemDetail bonoSinViajes(BonoSinViajesException ex) {
        return problema(HttpStatus.CONFLICT, "Bono sin viajes", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail argumentoInvalido(IllegalArgumentException ex) {
        return problema(HttpStatus.BAD_REQUEST, "Petición no válida", ex.getMessage());
    }

    // Lanzada por @Version cuando dos peticiones modifican el mismo bono a la vez
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ProblemDetail conflictoConcurrencia(ObjectOptimisticLockingFailureException ex) {
        return problema(HttpStatus.CONFLICT, "Operación simultánea",
                "El bono se ha modificado a la vez desde otra petición. Inténtalo de nuevo.");
    }

    private ProblemDetail problema(HttpStatus status, String titulo, String detalle) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detalle);
        problem.setTitle(titulo);
        return problem;
    }
}

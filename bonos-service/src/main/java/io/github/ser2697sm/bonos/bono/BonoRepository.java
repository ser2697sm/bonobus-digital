package io.github.ser2697sm.bonos.bono;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BonoRepository extends JpaRepository<Bono, Long> {

    // Spring genera la consulta a partir del nombre: SELECT ... FROM bono WHERE codigo = ?
    Optional<Bono> findByCodigo(String codigo);
}

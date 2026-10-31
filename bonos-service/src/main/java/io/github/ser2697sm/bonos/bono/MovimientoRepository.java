package io.github.ser2697sm.bonos.bono;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    // Historial paginado, del más reciente al más antiguo
    Page<Movimiento> findByBonoOrderByFechaDesc(Bono bono, Pageable pageable);
}

package io.github.ser2697sm.bonos.bono;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Coordina las operaciones con bonos: busca en base de datos, llama a la lógica
 * del Bono y guarda el resultado. Las reglas de negocio viven en la entidad Bono.
 */
@Slf4j
@Service
@RequiredArgsConstructor // Lombok genera el constructor para inyectar los repositorios
public class BonoService {

    private final BonoRepository bonoRepository;
    private final MovimientoRepository movimientoRepository;

    @Transactional
    public Bono crearBono(String titular) {
        Bono bono = new Bono(generarCodigo(), titular);
        return bonoRepository.save(bono);
    }

    @Transactional(readOnly = true)
    public Bono obtenerBono(String codigo) {
        return buscar(codigo);
    }

    @Transactional
    public Movimiento recargar(String codigo, int viajes) {
        Bono bono = buscar(codigo);
        Movimiento movimiento = bono.recargar(viajes);
        log.info("Bono {} recargado con {} viajes. Quedan {}", codigo, viajes, bono.getViajesRestantes());
        return movimientoRepository.save(movimiento);
    }

    @Transactional
    public Movimiento registrarViaje(String codigo) {
        Bono bono = buscar(codigo);
        Movimiento movimiento = bono.registrarViaje();

        // Alertas provisionales: de momento solo un log.
        // En la fase de Kafka, aquí publicaremos eventos y las generará el servicio de notificaciones.
        log.info("ALERTA: viaje descontado en el bono {}. Quedan {} viajes", codigo, bono.getViajesRestantes());
        if (bono.estaAgotado()) {
            log.info("ALERTA: el bono {} se ha quedado sin viajes. Hay que recargarlo", codigo);
        }

        return movimientoRepository.save(movimiento);
    }

    @Transactional(readOnly = true)
    public Page<Movimiento> historial(String codigo, Pageable pageable) {
        Bono bono = buscar(codigo);
        return movimientoRepository.findByBonoOrderByFechaDesc(bono, pageable);
    }

    private Bono buscar(String codigo) {
        return bonoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new BonoNoEncontradoException(codigo));
    }

    // Ej. LOR-3F9A1C2B. Suficiente para un proyecto de portfolio.
    private String generarCodigo() {
        return "LOR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

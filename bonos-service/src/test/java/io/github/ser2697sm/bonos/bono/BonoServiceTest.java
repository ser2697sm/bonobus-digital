package io.github.ser2697sm.bonos.bono;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios del BonoService con Mockito.
 * Los repositorios son mocks: no hay base de datos ni Spring.
 * Aquí no se prueban las reglas del Bono (eso es BonoTest), sino que el service
 * coordina bien: busca el bono, llama a su lógica y guarda el movimiento.
 *
 * Regla de oro: Arrange = los when(...) | Act = una llamada al service | Assert = asserts y verify(...)
 */
@ExtendWith(MockitoExtension.class) // activa Mockito: crea los @Mock y los inyecta en @InjectMocks
class BonoServiceTest {

    @Mock
    private BonoRepository bonoRepository;

    @Mock
    private MovimientoRepository movimientoRepository;

    @InjectMocks // service REAL con los dos repositorios falsos dentro
    private BonoService bonoService;

    @Test
    void crearBonoGuardaBonoConTitularYCodigo() {
        // Arrange: "cuando guarden un bono, devuelve el mismo bono que te pasan"
        when(bonoRepository.save(any(Bono.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Bono bono = bonoService.crearBono("Sergio");

        // Assert
        assertEquals("Sergio", bono.getTitular());
        assertTrue(bono.getCodigo().startsWith("LOR-"));
        assertEquals(0, bono.getViajesRestantes());
        verify(bonoRepository).save(bono);
    }

    @Test
    void obtenerBonoDevuelveElBonoSiExiste() {
        // Arrange: "cuando busquen LOR-TEST, devuelve este bono"
        Bono bono = new Bono("LOR-TEST", "Sergio");
        when(bonoRepository.findByCodigo("LOR-TEST")).thenReturn(Optional.of(bono));

        // Act
        Bono resultado = bonoService.obtenerBono("LOR-TEST");

        // Assert: es exactamente el bono que devolvió el repositorio
        assertSame(bono, resultado);
    }

    @Test
    void obtenerBonoInexistenteLanzaExcepcion() {
        // Arrange: "cuando busquen LOR-NOEXISTE, no encuentres nada"
        when(bonoRepository.findByCodigo("LOR-NOEXISTE")).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(BonoNoEncontradoException.class, () -> bonoService.obtenerBono("LOR-NOEXISTE"));
    }

    @Test
    void recargarSumaViajesYGuardaElMovimiento() {
        // Arrange
        Bono bono = new Bono("LOR-TEST", "Sergio");
        when(bonoRepository.findByCodigo("LOR-TEST")).thenReturn(Optional.of(bono));
        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Movimiento movimiento = bonoService.recargar("LOR-TEST", 8);

        // Assert
        assertEquals(8, bono.getViajesRestantes());
        assertEquals(TipoMovimiento.RECARGA, movimiento.getTipo());
        verify(movimientoRepository).save(movimiento);
    }

    @Test
    void registrarViajeDescuentaViajeYGuardaElMovimiento() {
        // Arrange: un bono con 8 viajes que el repositorio "encuentra"
        Bono bono = new Bono("LOR-TEST", "Sergio");
        bono.recargar(8);
        when(bonoRepository.findByCodigo("LOR-TEST")).thenReturn(Optional.of(bono));
        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Movimiento movimiento = bonoService.registrarViaje("LOR-TEST");

        // Assert
        assertEquals(7, bono.getViajesRestantes());
        assertEquals(TipoMovimiento.VIAJE, movimiento.getTipo());
        verify(movimientoRepository).save(movimiento);
    }

    @Test
    void registrarViajeConBonoInexistenteLanzaExcepcionYNoGuarda() {
        // Arrange: el bono no existe. Sin when del save: el service nunca llega a guardar
        when(bonoRepository.findByCodigo("LOR-NOEXISTE")).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(BonoNoEncontradoException.class, () -> bonoService.registrarViaje("LOR-NOEXISTE"));
        verify(movimientoRepository, never()).save(any()); // no se ha guardado ningún movimiento
    }

    @Test
    void registrarViajeSinViajesLanzaExcepcionYNoGuarda() {
        // Arrange: el bono existe pero tiene 0 viajes
        Bono bono = new Bono("LOR-TEST", "Sergio");
        when(bonoRepository.findByCodigo("LOR-TEST")).thenReturn(Optional.of(bono));

        // Act + Assert
        assertThrows(BonoSinViajesException.class, () -> bonoService.registrarViaje("LOR-TEST"));
        assertEquals(0, bono.getViajesRestantes());
        verify(movimientoRepository, never()).save(any()); // no hay apunte falso en el historial
    }
}

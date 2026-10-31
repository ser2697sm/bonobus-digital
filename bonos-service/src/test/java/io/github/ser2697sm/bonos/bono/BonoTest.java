package io.github.ser2697sm.bonos.bono;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios de las reglas de negocio del Bono.
 * Java puro: sin Spring, sin base de datos, sin mocks.
 * Patrón Arrange - Act - Assert.
 */
class BonoTest {

    @Test
    void bonoNuevoEmpiezaSinViajesYAgotado() {
        // Arrange
        Bono bono = new Bono("LOR-TEST", "Sergio");

        // Assert
        assertEquals(0, bono.getViajesRestantes());
        assertTrue(bono.estaAgotado());
    }

    @Test
    void recargarSumaViajesYDevuelveMovimientoRecarga() {
        // Arrange
        Bono bono = new Bono("LOR-TEST", "Sergio");

        // Act
        Movimiento movimiento = bono.recargar(8);

        // Assert
        assertEquals(8, bono.getViajesRestantes());
        assertEquals(TipoMovimiento.RECARGA, movimiento.getTipo());
        assertEquals(8, movimiento.getViajes());
    }

    @Test
    void recargarCeroLanzaExcepcion() {
        // Arrange
        Bono bono = new Bono("LOR-TEST", "Sergio");

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> bono.recargar(0));
        assertEquals(0, bono.getViajesRestantes());
    }

    @Test
    void recargarNegativoLanzaExcepcion() {
        // Arrange
        Bono bono = new Bono("LOR-TEST", "Sergio");

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> bono.recargar(-1));
        assertEquals(0, bono.getViajesRestantes());
    }

    @Test
    void viajarRestaUnViajeYDevuelveMovimientoViaje() {
        // Arrange
        Bono bono = new Bono("LOR-TEST", "Sergio");
        bono.recargar(8);

        // Act
        Movimiento movimiento = bono.registrarViaje();

        // Assert
        assertEquals(7, bono.getViajesRestantes());
        assertEquals(TipoMovimiento.VIAJE, movimiento.getTipo());
        assertEquals(-1, movimiento.getViajes());
    }

    @Test
    void viajarSinViajesLanzaExcepcion() {
        // Arrange
        Bono bono = new Bono("LOR-TEST", "Sergio");

        // Act + Assert
        assertThrows(BonoSinViajesException.class, bono::registrarViaje);
        assertEquals(0, bono.getViajesRestantes());
    }

    @Test
    void gastarElUltimoViajeDejaElBonoAgotado() {
        // Arrange
        Bono bono = new Bono("LOR-TEST", "Sergio");
        bono.recargar(1);

        // Act
        bono.registrarViaje();

        // Assert
        assertEquals(0, bono.getViajesRestantes());
        assertTrue(bono.estaAgotado());
    }
}

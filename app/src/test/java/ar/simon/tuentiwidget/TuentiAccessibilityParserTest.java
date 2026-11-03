package ar.simon.tuentiwidget;

import org.junit.Test;

import static org.junit.Assert.*;

public class TuentiAccessibilityParserTest {
    @Test
    public void parserReadsQuotaCard() {
        BalanceData balance = TuentiAccessibilityParser.parse(
                "Tuenti\nConsumos\nDatos disponibles\n7,4 GB\nde 12 GB");

        assertNotNull(balance);
        assertFalse(balance.noCombo);
        assertEquals(7.4 * 1024 * 1024 * 1024,
                balance.remainingBytes, 1);
    }

    @Test
    public void parserReadsActiveComboHitCardFromCurrentHomeScreen() {
        BalanceData balance = TuentiAccessibilityParser.parse(
                "Hola Tuenti\nCombo Hit\nDisponibles\n8 GB\nde 8 GB\n"
                        + "Gigas\nVence en");

        assertNotNull(balance);
        assertFalse(balance.noCombo);
        assertEquals(8L * 1024 * 1024 * 1024, balance.remainingBytes);
        assertEquals(8L * 1024 * 1024 * 1024, balance.initialBytes);
    }

    @Test
    public void parserRecognizesCapturedNoComboHierarchy() {
        String hierarchy = "Hola Tuenti\n"
                + "0342 Línea móvil - Prepago\n"
                + "Recarga automática\nÚltimos movimientos\n"
                + "Tarjetas\nTodavía no tenés Gigas\n"
                + "Empezá por comprar el combo que más te convenga.\n"
                + "Comprar Combo\nSaldo\n$ 0,00\nServicios\nCuenta";
        BalanceData balance = TuentiAccessibilityParser.parse(hierarchy);

        assertNotNull(balance);
        assertTrue(balance.noCombo);
    }
}

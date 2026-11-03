package ar.simon.tuentiwidget;

import org.junit.Test;

import static org.junit.Assert.*;

public class BalanceDataTest {
    @Test
    public void parsesAvailableAndTotalGigabytes() {
        BalanceData balance = BalanceData.fromPageText(
                "Consumos\nDatos disponibles\n5,25 GB\nde 8 GB\nVence 07/11/2026");

        assertNotNull(balance);
        assertFalse(balance.noCombo);
        assertEquals(5.25 * 1024 * 1024 * 1024,
                balance.remainingBytes, 1);
        assertEquals(8L * 1024 * 1024 * 1024, balance.initialBytes);
        assertEquals(34, balance.usedPercent());
    }

    @Test
    public void recognizesRealTuentiNoComboScreen() {
        BalanceData balance = BalanceData.fromPageText(
                "Hola Tuenti\nTodavía no tenés Gigas\n"
                        + "Empezá por comprar el combo que\n"
                        + "más te convenga.\nComprar Combo\nSaldo\n$ 0,00");

        assertNotNull(balance);
        assertTrue(balance.noCombo);
        assertEquals(0, balance.initialBytes);
        assertEquals(0, balance.remainingBytes);
    }

    @Test
    public void noComboDetectionIgnoresAccentsAndCase() {
        BalanceData balance = BalanceData.fromPageText(
                "TODAVIA NO TENES GIGAS\nComprar Combo");
        assertNotNull(balance);
        assertTrue(balance.noCombo);
    }

    @Test
    public void rejectsUnrelatedScreen() {
        assertNull(BalanceData.fromPageText("Inicio\nComprar gigas\n1 GB"));
    }
}

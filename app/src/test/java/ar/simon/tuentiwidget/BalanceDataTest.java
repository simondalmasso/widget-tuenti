package ar.simon.tuentiwidget;
import org.junit.Test;
import static org.junit.Assert.*;
public class BalanceDataTest {
 @Test public void parsesAvailableAndTotalGigabytes() {
  BalanceData b=BalanceData.fromPageText("Consumos\nDatos disponibles\n5,25 GB\nde 8 GB\nVence 07/11/2026");
  assertNotNull(b); assertEquals(5.25*1024*1024*1024,b.remainingBytes,1); assertEquals(8L*1024*1024*1024,b.initialBytes);
  assertEquals(34,b.usedPercent());
 }
 @Test public void rejectsUnrelatedScreen() { assertNull(BalanceData.fromPageText("Inicio\nComprar gigas\n1 GB")); }
}

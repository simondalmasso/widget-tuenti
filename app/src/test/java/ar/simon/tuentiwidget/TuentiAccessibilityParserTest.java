package ar.simon.tuentiwidget;
import org.junit.Test;
import static org.junit.Assert.*;
public class TuentiAccessibilityParserTest {
 @Test public void parserReadsQuotaCard() {
  BalanceData b=TuentiAccessibilityParser.parse("Tuenti\nConsumos\nDatos disponibles\n7,4 GB\nde 12 GB");
  assertNotNull(b); assertEquals(7.4*1024*1024*1024,b.remainingBytes,1);
 }
}

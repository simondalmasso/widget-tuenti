package ar.simon.tuentiwidget;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class WidgetRefreshContractTest {
    @Test public void refreshTargetsTheOfficialTuentiApplication() {
        assertEquals("com.tuentiargentina", WidgetRefreshContract.TUENTI_PACKAGE);
    }
}

package ar.simon.tuentiwidget;

import org.junit.Test;
import static org.junit.Assert.*;

public class WidgetRenderSpecTest {
    @Test
    public void matchesTheAvailableA22WidgetViewport() {
        WidgetRenderSpec spec = WidgetRenderSpec.forWidgetSize(347, 115, 1.875f, 1.875f);
        assertEquals(643, spec.bitmapWidthPx);
        assertEquals(208, spec.bitmapHeightPx);
    }

    @Test
    public void usesReadableSpTypographyIndependentOfDensity() {
        WidgetRenderSpec mdpi = WidgetRenderSpec.forWidgetSize(347, 115, 1f, 1f);
        WidgetRenderSpec hdpi = WidgetRenderSpec.forWidgetSize(347, 115, 1.875f, 1.875f);
        assertEquals(32f, mdpi.centerValueTextSizeSp, 0.01f);
        assertEquals(18f, mdpi.centerUnitTextSizeSp, 0.01f);
        assertEquals(26f, mdpi.detailTitleTextSizeSp, 0.01f);
        assertEquals(20f, mdpi.detailSubtitleTextSizeSp, 0.01f);
        assertEquals(mdpi.centerValueTextSizeSp, hdpi.centerValueTextSizeSp, 0.01f);
        assertEquals(mdpi.detailTitleTextSizeSp, hdpi.detailTitleTextSizeSp, 0.01f);
        assertTrue(hdpi.bitmapWidthPx > mdpi.bitmapWidthPx);
        assertTrue(hdpi.bitmapHeightPx > mdpi.bitmapHeightPx);
    }
}

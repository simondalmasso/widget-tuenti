package ar.simon.tuentiwidget;

final class WidgetRenderSpec {
    final int bitmapWidthPx;
    final int bitmapHeightPx;
    final float centerValueTextSizeSp;
    final float centerUnitTextSizeSp;
    final float detailTitleTextSizeSp;
    final float detailSubtitleTextSizeSp;

    private WidgetRenderSpec(int width, int height, float centerValue, float centerUnit,
                             float detailTitle, float detailSubtitle) {
        bitmapWidthPx = width;
        bitmapHeightPx = height;
        centerValueTextSizeSp = centerValue;
        centerUnitTextSizeSp = centerUnit;
        detailTitleTextSizeSp = detailTitle;
        detailSubtitleTextSizeSp = detailSubtitle;
    }

    static WidgetRenderSpec forWidgetSize(int widthDp, int heightDp,
                                          float density, float scaledDensity) {
        int contentWidthDp = Math.max(1, widthDp - 4);
        int contentHeightDp = Math.max(1, heightDp - 4);
        return new WidgetRenderSpec(Math.round(contentWidthDp * density),
                Math.round(contentHeightDp * density),
                32f, 18f, 26f, 20f);
    }
}

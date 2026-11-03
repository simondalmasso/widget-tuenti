package ar.simon.tuentiwidget;

final class WidgetRenderSpec {
    final int bitmapWidthPx;
    final int bitmapHeightPx;
    final float centerValueTextSizeSp;
    final float centerUnitTextSizeSp;
    final float detailTitleTextSizeSp;
    final float detailSubtitleTextSizeSp;
    final boolean compactLayout;
    final boolean miniLayout;
    final boolean showDetails;
    final boolean showRefreshButton;

    private WidgetRenderSpec(int width, int height, float centerValue, float centerUnit,
                             float detailTitle, float detailSubtitle,
                             boolean compact, boolean mini,
                             boolean details, boolean refresh) {
        bitmapWidthPx = width;
        bitmapHeightPx = height;
        centerValueTextSizeSp = centerValue;
        centerUnitTextSizeSp = centerUnit;
        detailTitleTextSizeSp = detailTitle;
        detailSubtitleTextSizeSp = detailSubtitle;
        compactLayout = compact;
        miniLayout = mini;
        showDetails = details;
        showRefreshButton = refresh;
    }

    static WidgetRenderSpec forWidgetSize(int widthDp, int heightDp,
                                          float density, float scaledDensity) {
        int contentWidthDp = Math.max(1, widthDp - 4);
        int contentHeightDp = Math.max(1, heightDp - 4);
        boolean mini = contentWidthDp < 105 || contentHeightDp < 52;
        boolean compact = !mini && (contentWidthDp < 220 || contentHeightDp < 68);

        // Keep normal typography fixed while the drawing geometry changes.
        // Only the physically tiny minimum-size widget uses a one-line label.
        float centerValue = mini ? 20f : 32f;
        float centerUnit = mini ? 14f : 18f;

        return new WidgetRenderSpec(
                Math.round(contentWidthDp * density),
                Math.round(contentHeightDp * density),
                centerValue, centerUnit, 26f, 20f,
                compact, mini, !compact && !mini,
                contentWidthDp >= 50 && contentHeightDp >= 52);
    }
}

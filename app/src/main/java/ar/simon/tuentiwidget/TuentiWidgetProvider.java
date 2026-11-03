package ar.simon.tuentiwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.RemoteViews;

public final class TuentiWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
        for (int id : ids) update(context, manager, id);
    }

    static void updateAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        for (int id : manager.getAppWidgetIds(
                new ComponentName(context, TuentiWidgetProvider.class))) {
            update(context, manager, id);
        }
    }

    private static void update(Context context, AppWidgetManager manager, int id) {
        RemoteViews views = new RemoteViews(
                context.getPackageName(), R.layout.tuenti_widget);
        views.setImageViewBitmap(R.id.usage_gauge,
                render(context, manager.getAppWidgetOptions(id), BalanceCache.load(context)));
        manager.updateAppWidget(id, views);
    }

    private static Bitmap render(Context context, Bundle options, BalanceData balance) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int widthDp = dimension(options, AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,
                AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 347);
        int heightDp = dimension(options, AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,
                AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 115);
        WidgetRenderSpec spec = WidgetRenderSpec.forWidgetSize(
                widthDp, heightDp, metrics.density, metrics.scaledDensity);

        Bitmap bitmap = Bitmap.createBitmap(spec.bitmapWidthPx,
                spec.bitmapHeightPx, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        float density = metrics.density;
        float scaledDensity = metrics.scaledDensity;
        float width = spec.bitmapWidthPx;
        float height = spec.bitmapHeightPx;
        float centerY = height / 2f;
        float ringCenterX = Math.min(height * 0.43f, width * 0.19f);
        float radius = Math.min(height * 0.37f, width * 0.145f);
        float stroke = 6f * density;
        RectF ring = new RectF(ringCenterX - radius, centerY - radius,
                ringCenterX + radius, centerY + radius);

        paint.setColor(0xFF354752);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(stroke);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawArc(ring, -90, 360, false, paint);

        if (balance != null && !balance.noCombo
                && balance.initialBytes > 0 && balance.remainingBytes >= 0) {
            paint.setColor(0xFF00D6B2);
            canvas.drawArc(ring, -90,
                    360f * balance.usedPercent() / 100f, false, paint);
        }

        float textX = ring.right + 14f * density;
        float textY = centerY - 5f * density;
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(0xFFFFFFFF);

        if (balance != null && balance.noCombo) {
            paint.setTextSize(spec.centerValueTextSizeSp * scaledDensity);
            canvas.drawText("SIN", ringCenterX, centerY - 1f * density, paint);
            paint.setTextSize(spec.centerUnitTextSizeSp * scaledDensity);
            canvas.drawText("COMBO", ringCenterX, centerY + 25f * density, paint);

            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(spec.detailTitleTextSizeSp * scaledDensity);
            canvas.drawText("No hay combo", textX, textY, paint);
            paint.setTextSize(spec.detailSubtitleTextSizeSp * scaledDensity);
            paint.setColor(0xFFB5C6D2);
            canvas.drawText("activo", textX, textY + 27f * density, paint);
            return bitmap;
        }

        String remaining = balance == null ? "--" : balance.remainingGb();
        paint.setTextSize(spec.centerValueTextSizeSp * scaledDensity);
        canvas.drawText(remaining, ringCenterX, centerY - 1f * density, paint);
        paint.setTextSize(spec.centerUnitTextSizeSp * scaledDensity);
        paint.setColor(0xFFB5C6D2);
        canvas.drawText("GB", ringCenterX, centerY + 25f * density, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(spec.detailTitleTextSizeSp * scaledDensity);
        paint.setColor(0xFFFFFFFF);
        canvas.drawText(balance == null ? "--"
                : String.format(java.util.Locale.US, "%.1f GB",
                balance.initialBytes / (1024d * 1024d * 1024d)), textX, textY, paint);
        paint.setTextSize(spec.detailSubtitleTextSizeSp * scaledDensity);
        paint.setColor(0xFFB5C6D2);
        canvas.drawText(balance == null ? ""
                : String.format(java.util.Locale.US, "%.0f%% usado",
                (double) balance.usedPercent()), textX, textY + 27f * density, paint);
        return bitmap;
    }

    private static int dimension(Bundle options, String preferred, String fallback, int defaultValue) {
        if (options == null) return defaultValue;
        int value = options.getInt(preferred, 0);
        return value > 0 ? value : options.getInt(fallback, defaultValue);
    }
}

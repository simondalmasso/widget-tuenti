package ar.simon.tuentiwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SizeF;
import android.view.View;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class TuentiWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
        for (int id : ids) update(context, manager, id);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager manager,
                                          int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, manager, appWidgetId, newOptions);
        update(context, manager, appWidgetId);
    }

    static void updateAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        for (int id : manager.getAppWidgetIds(
                new ComponentName(context, TuentiWidgetProvider.class))) {
            update(context, manager, id);
        }
    }

    private static void update(Context context, AppWidgetManager manager, int id) {
        Bundle options = manager.getAppWidgetOptions(id);
        BalanceData balance = BalanceCache.load(context);
        RemoteViews views = responsiveViews(context, id, options, balance);
        manager.updateAppWidget(id, views);
    }

    private static RemoteViews responsiveViews(Context context, int id,
                                                Bundle options, BalanceData balance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && options != null) {
            ArrayList<SizeF> sizes = options.getParcelableArrayList(
                    AppWidgetManager.OPTION_APPWIDGET_SIZES);
            if (sizes != null && !sizes.isEmpty()) {
                Map<SizeF, RemoteViews> variants = new HashMap<>();
                for (SizeF size : sizes) {
                    int widthDp = Math.max(1, Math.round(size.getWidth()));
                    int heightDp = Math.max(1, Math.round(size.getHeight()));
                    variants.put(size, createViews(context, id, balance, widthDp, heightDp));
                }
                return new RemoteViews(variants);
            }
        }

        int widthDp = dimension(options, AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,
                AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 347);
        int heightDp = dimension(options, AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT,
                AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 115);
        return createViews(context, id, balance, widthDp, heightDp);
    }

    private static RemoteViews createViews(Context context, int id, BalanceData balance,
                                           int widthDp, int heightDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        WidgetRenderSpec spec = WidgetRenderSpec.forWidgetSize(
                widthDp, heightDp, metrics.density, metrics.scaledDensity);
        RemoteViews views = new RemoteViews(
                context.getPackageName(), R.layout.tuenti_widget);
        views.setImageViewBitmap(R.id.usage_gauge, render(context, spec, balance));

        if (spec.showRefreshButton) {
            Intent launch = context.getPackageManager().getLaunchIntentForPackage(
                    WidgetRefreshContract.TUENTI_PACKAGE);
            if (launch != null) {
                launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent refreshPendingIntent = PendingIntent.getActivity(context, id, launch,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                views.setViewVisibility(R.id.refresh_button, View.VISIBLE);
                views.setOnClickPendingIntent(R.id.refresh_button, refreshPendingIntent);
            }
        } else {
            views.setViewVisibility(R.id.refresh_button, View.GONE);
        }
        return views;
    }

    private static Bitmap render(Context context, WidgetRenderSpec spec, BalanceData balance) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        Bitmap bitmap = Bitmap.createBitmap(spec.bitmapWidthPx,
                spec.bitmapHeightPx, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        float density = metrics.density;
        float scaledDensity = metrics.scaledDensity;
        float width = spec.bitmapWidthPx;
        float height = spec.bitmapHeightPx;
        float centerY = height / 2f;

        if (spec.miniLayout) {
            drawMiniLabel(canvas, paint, spec, balance, width, centerY, scaledDensity);
            return bitmap;
        }

        float ringCenterX = spec.compactLayout ? width / 2f
                : Math.min(height * 0.43f, width * 0.19f);
        float radius = Math.min(height * 0.37f,
                width * (spec.compactLayout ? 0.20f : 0.145f));
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

        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(0xFFFFFFFF);

        if (spec.compactLayout) {
            drawCompactLabel(canvas, paint, spec, balance,
                    ringCenterX, centerY, radius, density, scaledDensity);
            return bitmap;
        }

        float textX = ring.right + 14f * density;
        float textY = centerY - 4f * density;
        if (balance != null && balance.noCombo) {
            paint.setTextSize(spec.centerValueTextSizeSp * scaledDensity);
            canvas.drawText("SIN", ringCenterX, centerY - 1f * density, paint);
            paint.setTextSize(spec.centerUnitTextSizeSp * scaledDensity);
            canvas.drawText("COMBO", ringCenterX, centerY + 22f * density, paint);

            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(spec.detailTitleTextSizeSp * scaledDensity);
            canvas.drawText("No hay combo", textX, textY, paint);
            paint.setTextSize(spec.detailSubtitleTextSizeSp * scaledDensity);
            paint.setColor(0xFFB5C6D2);
            canvas.drawText("activo", textX, centerY + 22f * density, paint);
            return bitmap;
        }

        String remaining = balance == null ? "--" : balance.remainingGb();
        paint.setTextSize(spec.centerValueTextSizeSp * scaledDensity);
        canvas.drawText(remaining, ringCenterX, centerY - 1f * density, paint);
        paint.setTextSize(spec.centerUnitTextSizeSp * scaledDensity);
        paint.setColor(0xFFB5C6D2);
        canvas.drawText("GB", ringCenterX, centerY + 22f * density, paint);

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
                (double) balance.usedPercent()), textX, centerY + 22f * density, paint);
        return bitmap;
    }

    private static void drawCompactLabel(Canvas canvas, Paint paint,
                                         WidgetRenderSpec spec, BalanceData balance,
                                         float centerX, float centerY, float radius,
                                         float density, float scaledDensity) {
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(0xFFFFFFFF);
        if (balance != null && balance.noCombo) {
            paint.setTextSize(24f * scaledDensity);
            canvas.drawText("SIN", centerX, centerY - 2f * density, paint);
            paint.setTextSize(14f * scaledDensity);
            paint.setColor(0xFFB5C6D2);
            canvas.drawText("COMBO", centerX,
                    centerY + Math.min(17f * density, radius * 0.72f), paint);
            return;
        }

        paint.setTextSize(spec.centerValueTextSizeSp * scaledDensity);
        canvas.drawText(balance == null ? "--" : balance.remainingGb(),
                centerX, centerY - 2f * density, paint);
        paint.setTextSize(spec.centerUnitTextSizeSp * scaledDensity);
        paint.setColor(0xFFB5C6D2);
        canvas.drawText("GB", centerX,
                centerY + Math.min(17f * density, radius * 0.72f), paint);
    }

    private static void drawMiniLabel(Canvas canvas, Paint paint, WidgetRenderSpec spec,
                                      BalanceData balance, float width, float centerY,
                                      float scaledDensity) {
        String label = balance == null ? "-- GB"
                : balance.noCombo ? "SIN COMBO" : balance.remainingGb() + " GB";
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(0xFFFFFFFF);
        paint.setTextSize((balance != null && balance.noCombo ? 16f
                : spec.centerValueTextSizeSp) * scaledDensity);
        float baseline = centerY - (paint.ascent() + paint.descent()) / 2f;
        canvas.drawText(label, width / 2f, baseline, paint);
    }

    private static int dimension(Bundle options, String preferred,
                                 String fallback, int defaultValue) {
        if (options == null) return defaultValue;
        int value = options.getInt(preferred, 0);
        return value > 0 ? value : options.getInt(fallback, defaultValue);
    }
}

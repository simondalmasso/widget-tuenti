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
        views.setImageViewBitmap(R.id.usage_gauge, render(BalanceCache.load(context)));
        manager.updateAppWidget(id, views);
    }

    private static Bitmap render(BalanceData balance) {
        int width = 600;
        int height = 300;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setColor(0xFF354752);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(30);
        paint.setStrokeCap(Paint.Cap.ROUND);
        RectF ring = new RectF(35, 35, 265, 265);
        canvas.drawArc(ring, -90, 360, false, paint);

        if (balance != null && !balance.noCombo
                && balance.initialBytes > 0 && balance.remainingBytes >= 0) {
            paint.setColor(0xFF00D6B2);
            canvas.drawArc(ring, -90,
                    360f * balance.usedPercent() / 100f, false, paint);
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFFFFFFF);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));

        if (balance != null && balance.noCombo) {
            paint.setTextSize(42);
            canvas.drawText("SIN", 150, 140, paint);
            paint.setTextSize(25);
            canvas.drawText("COMBO", 150, 176, paint);

            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTextSize(34);
            canvas.drawText("No hay combo", 315, 126, paint);
            paint.setTextSize(30);
            paint.setColor(0xFFB5C6D2);
            canvas.drawText("activo", 315, 166, paint);
            return bitmap;
        }

        String remaining = balance == null ? "--" : balance.remainingGb();
        paint.setTextSize(48);
        canvas.drawText(remaining, 150, 145, paint);
        paint.setTextSize(22);
        paint.setColor(0xFFB5C6D2);
        canvas.drawText("GB", 150, 178, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(54);
        paint.setColor(0xFFFFFFFF);
        canvas.drawText(balance == null ? "--"
                : String.format(java.util.Locale.US, "%.1f GB",
                balance.initialBytes / (1024d * 1024d * 1024d)), 315, 140, paint);
        paint.setTextSize(25);
        paint.setColor(0xFFB5C6D2);
        canvas.drawText(balance == null ? ""
                : String.format(java.util.Locale.US, "%.0f%% usado",
                (double) balance.usedPercent()), 315, 188, paint);
        return bitmap;
    }
}

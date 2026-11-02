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
    @Override public void onUpdate(Context c,AppWidgetManager m,int[] ids) { for(int id:ids) update(c,m,id); }
    static void updateAll(Context c) {
        AppWidgetManager m=AppWidgetManager.getInstance(c);
        for(int id:m.getAppWidgetIds(new ComponentName(c,TuentiWidgetProvider.class))) update(c,m,id);
    }
    private static void update(Context c,AppWidgetManager m,int id) {
        RemoteViews v=new RemoteViews(c.getPackageName(),R.layout.tuenti_widget);
        v.setImageViewBitmap(R.id.usage_gauge,render(BalanceCache.load(c)));
        m.updateAppWidget(id,v);
    }
    private static Bitmap render(BalanceData b) {
        int w=600,h=300;
        Bitmap bmp=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        Canvas c=new Canvas(bmp);
        Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(0xFF354752); p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(30); p.setStrokeCap(Paint.Cap.ROUND);
        RectF ring=new RectF(35,35,265,265);
        c.drawArc(ring,-90,360,false,p);
        if(b!=null && b.initialBytes>0 && b.remainingBytes>=0) {
            p.setColor(0xFF00D6B2);
            c.drawArc(ring,-90,360f*b.usedPercent()/100f,false,p);
        }
        p.setStyle(Paint.Style.FILL); p.setColor(0xFFFFFFFF); p.setTextAlign(Paint.Align.CENTER);
        p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD)); p.setTextSize(48);
        String remaining=b==null?"--":b.remainingGb();
        c.drawText(remaining,150,145,p);
        p.setTextSize(22); p.setColor(0xFFB5C6D2);
        c.drawText("GB",150,178,p);
        p.setTextAlign(Paint.Align.LEFT); p.setTextSize(54); p.setColor(0xFFFFFFFF);
        c.drawText(b==null?"--":String.format(java.util.Locale.US,"%.1f GB",b.initialBytes/(1024d*1024d*1024d)),315,140,p);
        p.setTextSize(25); p.setColor(0xFFB5C6D2);
        c.drawText(b==null?"":String.format(java.util.Locale.US,"%.0f%% usado",(double)b.usedPercent()),315,188,p);
        return bmp;
    }
}

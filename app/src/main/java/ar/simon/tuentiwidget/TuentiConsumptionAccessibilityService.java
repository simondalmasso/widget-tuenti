package ar.simon.tuentiwidget;

import android.accessibilityservice.AccessibilityService;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public final class TuentiConsumptionAccessibilityService extends AccessibilityService {
    private static final String PACKAGE="com.tuentiargentina";
    private final Handler handler=new Handler(Looper.getMainLooper());
    private final Runnable reader=this::read;
    @Override public void onAccessibilityEvent(AccessibilityEvent e) {
        if(e==null || e.getPackageName()==null || !PACKAGE.contentEquals(e.getPackageName())) return;
        handler.removeCallbacks(reader); handler.postDelayed(reader,500);
    }
    private void read() {
        AccessibilityNodeInfo root=getRootInActiveWindow();
        if(root==null) return;
        try {
            if(root.getPackageName()==null || !PACKAGE.contentEquals(root.getPackageName())) return;
            StringBuilder text=new StringBuilder();
            collect(root,text,0);
            BalanceData data=TuentiAccessibilityParser.parse(text.toString());
            if(data!=null && (data.remainingBytes==-1 || data.initialBytes>0)) {
                BalanceCache.save(this,data);
                TuentiWidgetProvider.updateAll(this);
            }
        } finally { root.recycle(); }
    }
    private void collect(AccessibilityNodeInfo n,StringBuilder out,int depth) {
        if(n==null || depth>35 || out.length()>20000) return;
        if(n.getText()!=null) out.append(n.getText()).append('\n');
        if(n.getContentDescription()!=null) out.append(n.getContentDescription()).append('\n');
        for(int i=0;i<n.getChildCount();i++) {
            AccessibilityNodeInfo c=n.getChild(i);
            if(c!=null) { collect(c,out,depth+1); c.recycle(); }
        }
    }
    @Override public void onInterrupt() { handler.removeCallbacksAndMessages(null); }
    @Override public void onDestroy() { handler.removeCallbacksAndMessages(null); super.onDestroy(); }
}

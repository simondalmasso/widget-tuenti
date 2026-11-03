package ar.simon.tuentiwidget;

import android.accessibilityservice.AccessibilityService;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public final class TuentiConsumptionAccessibilityService extends AccessibilityService {
    private static final String PACKAGE = "com.tuentiargentina";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable reader = this::read;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null || event.getPackageName() == null
                || !PACKAGE.contentEquals(event.getPackageName())) return;
        handler.removeCallbacks(reader);
        handler.postDelayed(reader, 500);
    }

    private void read() {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;
        try {
            if (root.getPackageName() == null
                    || !PACKAGE.contentEquals(root.getPackageName())) return;

            StringBuilder text = new StringBuilder();
            collect(root, text, 0);
            BalanceData data = TuentiAccessibilityParser.parse(text.toString());

            if (data != null && (data.noCombo
                    || data.remainingBytes == -1 || data.initialBytes > 0)) {
                BalanceCache.save(this, data);
                TuentiWidgetProvider.updateAll(this);
            }
        } finally {
            root.recycle();
        }
    }

    private void collect(AccessibilityNodeInfo node, StringBuilder out, int depth) {
        if (node == null || depth > 35 || out.length() > 20000) return;
        if (node.getText() != null) out.append(node.getText()).append('\n');
        if (node.getContentDescription() != null) {
            out.append(node.getContentDescription()).append('\n');
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                collect(child, out, depth + 1);
                child.recycle();
            }
        }
    }

    @Override
    public void onInterrupt() {
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}

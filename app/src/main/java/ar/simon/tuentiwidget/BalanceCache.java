package ar.simon.tuentiwidget;

import android.content.Context;
import android.content.SharedPreferences;

final class BalanceCache {
    private static final String PREF="balance_private";
    static void save(Context c, BalanceData b) {
        c.getSharedPreferences(PREF,Context.MODE_PRIVATE).edit()
            .putLong("remaining",b.remainingBytes).putLong("initial",b.initialBytes)
            .putString("expiry",b.expirationDate).putLong("updated",System.currentTimeMillis()).apply();
    }
    static BalanceData load(Context c) {
        SharedPreferences p=c.getSharedPreferences(PREF,Context.MODE_PRIVATE);
        if(!p.contains("initial")) return null;
        return new BalanceData(p.getLong("remaining",0),p.getLong("initial",0),p.getString("expiry",""),"Datos disponibles",p.getLong("updated",0));
    }
}

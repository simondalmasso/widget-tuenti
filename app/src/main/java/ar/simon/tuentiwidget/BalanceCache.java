package ar.simon.tuentiwidget;

import android.content.Context;
import android.content.SharedPreferences;

final class BalanceCache {
    private static final String PREF = "balance_private";
    private static final String STATUS = "status";
    private static final String SIN_COMBO = "SIN_COMBO";

    static void save(Context context, BalanceData balance) {
        SharedPreferences.Editor editor = context
                .getSharedPreferences(PREF, Context.MODE_PRIVATE).edit();

        if (balance.noCombo) {
            // Clear quota fields so an old combo can never be rendered as current.
            editor.clear()
                    .putString(STATUS, SIN_COMBO)
                    .putLong("updated", balance.updatedAt)
                    .apply();
            return;
        }

        editor.putString(STATUS, "AVAILABLE")
                .putLong("remaining", balance.remainingBytes)
                .putLong("initial", balance.initialBytes)
                .putString("expiry", balance.expirationDate)
                .putLong("updated", balance.updatedAt)
                .apply();
    }

    static BalanceData load(Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(PREF, Context.MODE_PRIVATE);

        if (SIN_COMBO.equals(preferences.getString(STATUS, ""))) {
            return BalanceData.noCombo(preferences.getLong("updated", 0));
        }
        if (!preferences.contains("initial")) return null;

        return new BalanceData(
                preferences.getLong("remaining", 0),
                preferences.getLong("initial", 0),
                preferences.getString("expiry", ""),
                "Datos disponibles",
                preferences.getLong("updated", 0));
    }
}

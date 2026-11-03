package ar.simon.tuentiwidget;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class BalanceData {
    final long remainingBytes, initialBytes, updatedAt;
    final String expirationDate, title;
    final boolean noCombo;

    BalanceData(long remaining, long initial, String expiry, String title) {
        this(remaining, initial, expiry, title, System.currentTimeMillis());
    }

    BalanceData(long remaining, long initial, String expiry, String title, long updated) {
        this(remaining, initial, expiry, title, updated, false);
    }

    private BalanceData(long remaining, long initial, String expiry, String title,
                        long updated, boolean noCombo) {
        remainingBytes = remaining;
        initialBytes = initial;
        expirationDate = expiry == null ? "" : expiry;
        this.title = title == null ? "" : title;
        updatedAt = updated;
        this.noCombo = noCombo;
    }

    static BalanceData noCombo() {
        return noCombo(System.currentTimeMillis());
    }

    static BalanceData noCombo(long updated) {
        return new BalanceData(0, 0, "", "Sin combo activo", updated, true);
    }

    long usedBytes() {
        return initialBytes > 0 && remainingBytes >= 0
                ? Math.max(0, initialBytes - remainingBytes) : 0;
    }

    int usedPercent() {
        return initialBytes > 0 && remainingBytes >= 0
                ? (int) Math.max(0, Math.min(100,
                Math.round(100d * usedBytes() / initialBytes))) : 0;
    }

    String remainingGb() {
        return formatGb(remainingBytes);
    }

    static String formatGb(long bytes) {
        if (bytes < 0) return "∞";
        return String.format(Locale.US, "%.1f",
                bytes / (1024d * 1024d * 1024d));
    }

    static BalanceData fromPageText(String text) {
        if (text == null) return null;

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT);

        // This screen means there is no active combo. It must override any
        // previously cached quota instead of being treated as a zero balance.
        if (normalized.contains("todavia no tenes gigas")) {
            return noCombo();
        }

        String[] lines = text.replace('\u00a0', ' ')
                .replace("\r", "\n").split("\n");
        int start = -1;
        boolean hasComboLabel = normalized.contains("combo");
        for (int i = 0; i < lines.length; i++) {
            String label = lines[i].trim();
            if (label.toLowerCase(Locale.ROOT).contains("datos disponibles")
                    || (hasComboLabel && label.equalsIgnoreCase("disponibles"))) {
                start = i;
                break;
            }
        }
        if (start < 0) return null;

        StringBuilder block = new StringBuilder();
        for (int i = start; i < Math.min(lines.length, start + 10); i++) {
            String line = lines[i].trim();
            String foldedLine = Normalizer.normalize(line, Normalizer.Form.NFD)
                    .replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT);
            if (i > start && foldedLine.contains("internet por dia")) break;
            block.append(line).append('\n');
        }

        Matcher matcher = Pattern.compile(
                "(?i)(ilimitado|[0-9]+(?:[.,][0-9]+)?\\s*(?:GB|MB|KB))")
                .matcher(block);
        if (!matcher.find()) return null;
        long remaining = parse(matcher.group());
        if (!matcher.find()) return null;
        long total = parse(matcher.group());
        if (total <= 0 && remaining != -1) return null;

        Matcher date = Pattern.compile("\\b[0-9]{1,2}/[0-9]{1,2}/[0-9]{2,4}\\b")
                .matcher(block);
        return new BalanceData(remaining, total,
                date.find() ? date.group() : "", "Datos disponibles");
    }

    private static long parse(String value) {
        if (value.equalsIgnoreCase("ilimitado")) return -1;
        Matcher matcher = Pattern.compile(
                "(?i)([0-9]+(?:[.,][0-9]+)?)\\s*(GB|MB|KB)")
                .matcher(value);
        if (!matcher.find()) return 0;
        double number = Double.parseDouble(matcher.group(1).replace(',', '.'));
        int power = matcher.group(2).equalsIgnoreCase("GB") ? 3
                : matcher.group(2).equalsIgnoreCase("MB") ? 2 : 1;
        return Math.round(number * Math.pow(1024, power));
    }
}

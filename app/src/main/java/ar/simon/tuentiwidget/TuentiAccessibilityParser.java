package ar.simon.tuentiwidget;

final class TuentiAccessibilityParser {
    static BalanceData parse(String text) { return BalanceData.fromPageText(text); }
}

// =============================
// File: src/Bookstore_gui/util/Money.java
// 통일된 가격 포맷(이미 있다면 스킵)
// =============================
package Bookstore_gui.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class Money {
    private static final NumberFormat F = NumberFormat.getCurrencyInstance(Locale.US);
    private Money(){}
    public static String fmt(double v){ return F.format(v); }
}
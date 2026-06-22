package com.bagas.stokikangiling.febri.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class FormatUtils {
    private static final Locale LOCALE_ID = new Locale("id", "ID");

    private FormatUtils() {
    }

    public static String rupiah(double value) {
        NumberFormat format = NumberFormat.getCurrencyInstance(LOCALE_ID);
        format.setMaximumFractionDigits(0);
        return format.format(value);
    }

    public static String kg(double value) {
        return String.format(LOCALE_ID, "%.2f kg", value);
    }

    public static String angka(double value) {
        return String.format(LOCALE_ID, "%.2f", value);
    }

    public static double parseDoubleFlexible(String text) throws NumberFormatException {
        if (text == null) {
            throw new NumberFormatException("Input kosong");
        }
        String normalized = text.trim().replace(",", ".");
        double value = Double.parseDouble(normalized);
        if (!Double.isFinite(value)) {
            throw new NumberFormatException("Input tidak valid");
        }
        return value;
    }
}

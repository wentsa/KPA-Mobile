package cz.knihaplnaaktivit.kpa_mobile.utilities;

import android.text.TextUtils;

import java.util.Collections;
import java.util.List;

public class Utils {

    /**
     * Formats currency.</br>
     * Examples:<br/>
     * <table>
     *     <tr><td><b>val</b></td><td><b>ret</b></td></tr>
     *     <tr><td>12</td><td>"12"</td></tr>
     *     <tr><td>12345</td><td>"12 345"</td></tr>
     *     <tr><td>123456789</td><td>"123 456 789"</td></tr>
     *     <tr><td>-15</td><td>"-15"</td></tr>
     *     <tr><td>-123456</td><td>"-123 456"</td></tr>
     * </table>
     *
     * @param val value
     */
    private static String formatCurrency(long val) {
        if (val == 0) {
            return "0";
        }

        boolean lesserThanZero = false;

        if (val < 0) {
            lesserThanZero = true;
            val = -val;
        }

        long x = val;
        boolean started = false;
        StringBuilder res = new StringBuilder();
        while (x > 0) {
            if (started) {
                res.insert(0, " ");
            }
            long toInsert = x % 1000;

            x /= 1000;

            if (x > 0) {
                res.insert(0, String.format("%3s", String.valueOf(toInsert)).replace(' ', '0'));
            } else {
                res.insert(0, String.valueOf(toInsert));
            }
            started = true;
        }
        if (lesserThanZero) {
            res.insert(0, "-");
        }
        return res.toString();
    }

    /**
     * Formats currency with shortcut.</br>
     * Examples:<br/>
     * <table>
     *     <tr><td><b>val</b></td><td><b>ret</b></td></tr>
     *     <tr><td>12</td><td>"12"</td></tr>
     *     <tr><td>12345</td><td>"12 tis"</td></tr>
     *     <tr><td>123456789</td><td>"123 mil"</td></tr>
     *     <tr><td>-15</td><td>"-15"</td></tr>
     *     <tr><td>-123456</td><td>"-123 tis"</td></tr>
     * </table>
     *
     * @param val value
     */
    private static String formatCurrencyShortcutted(int val) {
        // -253 / 253
        if(val < 1_000 && val > -1_000) {
            return String.valueOf(val);
        }
        // -1,3 tis / 1,3 tis
        if(val < 10_000 && val > -10_000) {
            return val / 1_000 + "," + Math.abs(val / 100) % 10 + " tis";
        }
        // -43 tis / 43 tis
        if(val < 1_000_000 && val > -1_000_000) {
            return val / 1_000 + " tis";
        }
        // -5,8 mil / 5,8 mil
        if(val < 10_000_000 && val > -10_000_000) {
            return val / 1_000_000 + "," + Math.abs(val / 100_000) % 10 + " mil";
        }
        // -12 mil / 12 mil
        if(val < 1_000_000_000 && val > -1_000_000_000) {
            return val / 1_000_000 + " mil";
        }
        // -2.1 mld / 2.1 mld
        return val / 1_000_000_000 + "," + Math.abs(val / 100_000_000) % 10 + " mld";
    }

    /**
     * Returns formatted currency value with appender <br/>
     * Eg.: for 123456 return "123 456 {append}"<br/>
     * @see #formatCurrency(long)
     * @param val value
     * @param appender appender
     * @return formated currency
     */
    public static String getCurrencyFormat(long val, String appender) {
        return Utils.formatCurrency(val) + " " + appender;
    }

    /**
     * Returns formatted currency value with shortcut and appender <br/>
     * Eg.: for 123456 return "123 tis {append}"<br/>
     * @see #formatCurrencyShortcutted(int)
     * @param val value
     * @param appender appender
     * @return formated currency
     */
    public static String getCurrencyFormatShortcutted(int val, String appender) {
        return Utils.formatCurrencyShortcutted(val) + " " + appender;
    }

    /**
     * Transforms null into empty list.
     */
    public static <T> List<T> nullToEmpty(List<T> list) {
        return list == null ? Collections.EMPTY_LIST : list;
    }

    /**
     * Checks email validity
     */
    public final static boolean isValidEmail(CharSequence mail) {
        return !TextUtils.isEmpty(mail) && android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }


}

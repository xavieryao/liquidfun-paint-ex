package org.papdt.liquidfunpaint.util;

import android.util.Log;

public class LogF {
    public static void d(String tag, String format, Object...args) {
        Log.d(tag, String.format(format, args));
    }

    public static void i(String tag, String format, Object...args) {
        Log.i(tag, String.format(format, args));
    }

    public static void v(String tag, String format, Object...args) {
        Log.v(tag, String.format(format, args));
    }
}

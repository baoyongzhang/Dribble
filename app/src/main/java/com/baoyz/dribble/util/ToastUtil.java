package com.baoyz.dribble.util;

import android.content.Context;
import android.content.res.Resources;
import android.widget.Toast;

public class ToastUtil {

    /**
     * Toast the long text
     *
     * @param context
     * @param text
     */
    public static void showLong(Context context, String text) {
        if (context == null || text == null)
            return;
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    /**
     * Toast the long text
     *
     * @param context
     * @param resId
     */
    public static void showLong(Context context, int resId) {
        if (context == null)
            return;
        Resources res = context.getResources();
        Toast.makeText(context, res.getString(resId), Toast.LENGTH_LONG).show();
    }

    /**
     * Toast the short text
     *
     * @param context
     * @param resId
     */
    public static void showShort(Context context, int resId) {
        if (context == null)
            return;
        Resources res = context.getResources();
        Toast.makeText(context, res.getString(resId), Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * Toast the short text
     *
     * @param context
     * @param text
     */
    public static void showShort(Context context, String text) {
        if (context == null || text == null)
            return;
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

}

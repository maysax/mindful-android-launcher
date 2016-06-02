package minium.co.core.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import minium.co.core.R;

/**
 * Created by Shahab on 6/2/2016.
 */
public class ThemeUtils {

    public static int getAccentColor(Context context) {
        return getThemeColor(context, R.attr.colorAccent);
    }

    public static int getPrimaryColor(Context context) {
        return getThemeColor(context, R.attr.colorPrimary);
    }

    public static int getPrimaryDarkColor(Context context) {
        return getThemeColor(context, R.attr.colorPrimaryDark);
    }

    private static int getThemeColor(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { attr });
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }
}

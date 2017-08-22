package minium.co.core.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import minium.co.core.R;


public class UIUtils {
    public static final String PACKAGE_NAME = "co.siempo.phone";

    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics());
    }

    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void toast(Context context, @StringRes int msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void toastShort(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void alert(Context context, String msg) {
        alert(context, null, msg);
    }

    public static void alert(Context context, String title, String msg) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    public static void alert(Context context, int layoutRes) {
        new AlertDialog.Builder(context)
                .setView(layoutRes)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    public static void confirm(Context context, String msg, DialogInterface.OnClickListener listener) {
        confirm(context, null, msg, listener);
    }

    public static void confirm(Context context, String title, String msg, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
    public static void confirmWithCancel(Context context, String title, String msg, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, listener)
                .show();
    }

    public static void ask(Context context, String msg, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(null)
                .setMessage(msg)
                .setPositiveButton(R.string.label_yes, listener)
                .setNegativeButton(R.string.label_no, null)
                .show();
    }

    public static void notification(Context context, String title, String msg, @StringRes int resOk, @StringRes int resCancel, @DrawableRes int resIcon, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setIcon(resIcon)
                .setPositiveButton(resOk, listener)
                .setNegativeButton(resCancel, listener)
                .show();
    }

    public static float getScreenHeight(Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return (float) displaymetrics.heightPixels;

    }

    public static void showKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * Hides the soft keyboard
     */
    public static void hideSoftKeyboard(Context mContext, IBinder windowToken) {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
    }

    public static int getDensity(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) metrics.density;
    }

    /**
     * This method returns the device model name.
     *
     * @param context refrence of calling view.
     * @return return the Manufacture Name,Model Name,Android Version and Display Size in pixel.
     */
    public static String getDeviceInfo(Context context) {
        return "\n\n\nMy Device Information is as follows:"
                +"\nMANUFACTURER : " + Build.MANUFACTURER
                + "\nMODEL : " + Build.MODEL
                + "\nVERSION : " + Build.VERSION.RELEASE
                + "\nDISPLAY : " + getScreenDisplaySize(context);
    }

    /**
     * This method returns the screen resolution.
     *
     * @param context takem the current view context.
     * @return Display size in pixel
     */
    private static String getScreenDisplaySize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        int width = size.x;
        return "" + width + "*" + height;
    }
}

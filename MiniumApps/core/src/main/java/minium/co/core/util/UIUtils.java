package minium.co.core.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.widget.Toast;

/**
 * Created by shahab on 12/21/15.
 */
public class UIUtils {

    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics());
    }

    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
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

}

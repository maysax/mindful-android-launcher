package minium.co.messages.ui.dialog;

import android.content.Context;
import android.os.Build;
import android.provider.Telephony;
import android.view.ViewGroup;

import minium.co.messages.R;

/**
 * Created by Shahab on 3/31/2016.
 */
public class DefaultSmsHelper {
    private Context mContext;
    private int mMessage;
    private static long sLastShown;
    private boolean mIsDefault = true;

    // Listener is currently useless because we can't listen for response from the system dialog
    public DefaultSmsHelper(Context context, int messageRes) {
        mContext = context;
        mMessage = messageRes != 0 ? messageRes : R.string.default_info;

        if (Build.VERSION.SDK_INT >= 19) {
            String defaultSmsPackage = Telephony.Sms.getDefaultSmsPackage(mContext);
            mIsDefault = defaultSmsPackage != null && defaultSmsPackage.equals(mContext.getPackageName());
        } else {
            mIsDefault = true;
        }
    }

    public void showIfNotDefault(ViewGroup viewGroup) {
        /* SKIP
        if (!mIsDefault) {
            long deltaTime = (System.nanoTime() / 1000000) - sLastShown;
            long duration = deltaTime > 60 * 1000 ? 8000 : 3000;

            Snackbar snackBar = Snackbar.with(mContext)
                    .type(getSnackBarType())
                    .text(mMessage)
                    .duration(duration)
                    .actionColor(ThemeManager.getColor())
                    .actionLabel(R.string.upgrade_now)
                    .actionListener(this);

            if (viewGroup == null) {
                SnackbarManager.show(snackBar);
            } else {
                SnackbarManager.show(snackBar, viewGroup);
            }

            sLastShown = System.nanoTime() / 1000000;
        }*/
    }
}

package com.moez.QKSMS.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.view.View;
import android.view.ViewGroup;

import com.moez.QKSMS.R;
import com.moez.QKSMS.ui.ThemeManager;
import com.moez.QKSMS.ui.base.QKActivity;


public class DefaultSmsHelper implements View.OnClickListener {

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

    public boolean showIfNotDefault(ViewGroup viewGroup) {
        if (!mIsDefault) {
            long deltaTime = (System.nanoTime() / 1000000) - sLastShown;
            long duration = deltaTime > 60 * 1000 ? 8000 : 3000;

            try {
                android.support.design.widget.Snackbar
                        .make(viewGroup == null ? ((QKActivity) mContext).findViewById(android.R.id.content) : viewGroup, mMessage, android.support.design.widget.Snackbar.LENGTH_LONG)
                        .setAction(R.string.upgrade_now, this).setActionTextColor(ThemeManager.getColor()).show();
            } catch (Exception e) {
                e.printStackTrace();
            }

            sLastShown = System.nanoTime() / 1000000;
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, mContext.getPackageName());
        mContext.startActivity(intent);
    }
}

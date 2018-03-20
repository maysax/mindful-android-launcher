package co.siempo.phone.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import co.siempo.phone.R;
import co.siempo.phone.event.CheckVersionEvent;
import co.siempo.phone.fragments.HelpFragment;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.log.Tracer;
import co.siempo.phone.service.ApiClient_;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.UIUtils;
import de.greenrobot.event.Subscribe;


public class HelpActivity extends CoreActivity {
    private String TAG = "HelpActivity";
    private long startTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        loadFragment(new HelpFragment(), R.id.helpView, "main");
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelper.getInstance().logScreenUsageTime(this.getClass().getSimpleName(), startTime);
    }


    @Subscribe
    public void checkVersionEvent(CheckVersionEvent event) {
        Log.d(TAG, "Check Version event...");
        if (event.getVersionName() != null && event.getVersionName().equalsIgnoreCase(CheckVersionEvent.ALPHA)) {
            if (event.getVersion() > UIUtils.getCurrentVersionCode(HelpActivity.this)) {
                Tracer.d("Installed version: " + UIUtils
                        .getCurrentVersionCode(HelpActivity.this) + " Found: " + event
                        .getVersion());
                showUpdateDialog(CheckVersionEvent.ALPHA);
            } else {
                ApiClient_.getInstance_(HelpActivity.this).checkAppVersion(CheckVersionEvent
                        .BETA);
            }
        } else {
            if (event.getVersion() > UIUtils.getCurrentVersionCode(HelpActivity.this)) {
                Tracer.d("Installed version: " + UIUtils
                        .getCurrentVersionCode(HelpActivity.this) + " Found: " + event
                        .getVersion());
                showUpdateDialog(CheckVersionEvent.BETA);
            } else {
                Tracer.d("Installed version: " + "Up to date.");
            }
        }
    }

    private void showUpdateDialog(String str) {

        PrefSiempo.getInstance(HelpActivity.this).write(PrefSiempo
                .IS_APP_INSTALLED_FIRSTTIME, false);
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context
                        .CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (connectivityManager != null) {
            activeNetwork = connectivityManager
                    .getActiveNetworkInfo();
        }
        if (activeNetwork != null) {
            UIUtils.confirmWithCancel(this, "", str.equalsIgnoreCase(CheckVersionEvent.ALPHA) ? "New alpha version found! Would you like to update Siempo?" : "New beta version found! Would you like to update Siempo?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        PrefSiempo.getInstance(HelpActivity.this).write
                                (PrefSiempo
                                        .UPDATE_PROMPT, false);
                        new ActivityHelper(HelpActivity.this).openBecomeATester();
                    }
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

        } else {
            Log.d(TAG, getString(R.string.nointernetconnection));
        }
    }

}

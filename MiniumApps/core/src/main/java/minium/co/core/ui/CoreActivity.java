package minium.co.core.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.R;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.config.Config;
import minium.co.core.event.DownloadApkEvent;
import minium.co.core.event.NFCEvent;
import minium.co.core.helper.Validate;
import minium.co.core.log.Tracer;
import minium.co.core.service.CoreAPIClient;
import minium.co.core.util.ActiveActivitiesTracker;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * This activity will be the base activity
 * All activity of all the modules should extend this activity
 *
 * Created by shahab on 3/17/16.
 */
@EActivity
public abstract class CoreActivity extends AppCompatActivity implements NFCInterface{


    int onStartCount = 0;

    @Pref
    protected DroidPrefs_ prefs;

    @SystemService
    protected ActivityManager activityManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //onCreateAnimation(savedInstanceState);

        if (prefs != null && prefs.selectedThemeId().get() != 0) {
            setTheme(prefs.selectedThemeId().get());
        }


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    private void onCreateAnimation(Bundle savedInstanceState) {
        onStartCount = 1;

        if (savedInstanceState == null) {
            this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
        } else {
            onStartCount = 2;
        }
    }

    private void onStartAnimation() {
        if (onStartCount > 1) {
            this.overridePendingTransition(R.anim.anim_slide_in_right,
                    R.anim.anim_slide_out_right);

        } else if (onStartCount == 1) {
            onStartCount++;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        ActiveActivitiesTracker.activityStarted();
        //onStartAnimation();
    }

    @Override
    protected void onStop() {
        ActiveActivitiesTracker.activityStopped();
        EventBus.getDefault().unregister(this);

        Log.i("onStop","MainActivity");

        try {
            if (Config.isNotificationAlive){
                //EventBus.getDefault().post(new NotificationTrayEvent(false));
                //this.getFragmentManager().beginTransaction().remove(NotificationFragment.this).commit();
                getFragmentManager().beginTransaction().
                        remove(getFragmentManager().findFragmentById(R.id.mainView)).commit();
                Config.isNotificationAlive = false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        super.onStop();
    }
    @Override
    protected void onPause() {

        Log.i("onPause","MainActivity");
        super.onPause();
    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }

    /**
     * Load fragment by replacing all previous fragments
     * @param fragment
     */
    public void loadFragment(Fragment fragment, int containerViewId, String tag) {
        try {
            FragmentManager fragmentManager = getFragmentManager();
            // clear back stack
            for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
                fragmentManager.popBackStack();
            }
            FragmentTransaction t = fragmentManager.beginTransaction();
            t.replace(containerViewId, fragment, tag);
            fragmentManager.popBackStack();
            // TODO: we have to allow state loss here
            // since this function can get called from an AsyncTask which
            // could be finishing after our app has already committed state
            // and is about to get shutdown.  What we *should* do is
            // not commit anything in an AsyncTask, but that's a bigger
            // change than we want now.
            t.commitAllowingStateLoss();
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
    }

    /**
     * Load Fragment on top of other fragments
     * @param fragment
     */
    public void loadChildFragment(Fragment fragment, int containerViewId) {
        Validate.notNull(fragment);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(containerViewId, fragment, "main")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                handleBackPress();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void genericEvent(Object event) {
        // DO NOT code here, it is a generic catch event method
    }

    @Override
    public void onBackPressed() {
        handleBackPress();
    }

    private void handleBackPress() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Subscribe
    public void downloadApkEvent(DownloadApkEvent event) {
        try {
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.setDataAndType(Uri.fromFile(new File(event.getPath())),
                    "application/vnd.android.package-archive");
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(installIntent);
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
    }

    @Override
    public String nfcRead(Tag t) {
        return null;
    }

    @Override
    public String readText(NdefRecord record) throws UnsupportedEncodingException {
        return null;
    }

    @Override
    public void nfcReader(Tag tag) {

    }
}

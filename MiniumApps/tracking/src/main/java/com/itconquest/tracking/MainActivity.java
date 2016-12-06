package com.itconquest.tracking;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.itconquest.tracking.services.GlobalTouchService_;
import com.itconquest.tracking.services.ScreenOnOffService_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import minium.co.core.app.DroidPrefs_;
import minium.co.core.ui.CoreActivity;

@EActivity(R.layout.activity_main)
public class MainActivity extends CoreActivity {

    public final static int REQUEST_CODE = 20;

    @ViewById
    Toolbar toolbar;

    @Pref
    DroidPrefs_ prefs;

    @ViewById
    FloatingActionButton fab;

    @AfterViews
    void afterViews() {
        setSupportActionBar(toolbar);
        loadViews();

    }

    private void checkDrawOverlayPermission() {
        /** check if we already  have permission to draw over other apps */
        if (!Settings.canDrawOverlays(this)) {
            /** if not construct intent to request permission */
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            /** request permission via start activity for result */
            startActivityForResult(intent, REQUEST_CODE);
        } else {
            if (prefs.isTrackingRunning().get()) {
                GlobalTouchService_.intent(getApplication()).start();
                ScreenOnOffService_.intent(getApplication()).start();
            } else {
                GlobalTouchService_.intent(getApplication()).stop();
                ScreenOnOffService_.intent(getApplication()).stop();
            }

        }
    }

    void loadViews() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (prefs.isTrackingRunning().get()) {
                    Snackbar.make(view, "Tracking paused", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fab.setImageResource(android.R.drawable.ic_media_play);

                } else {
                    Snackbar.make(view, "Tracking started", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    fab.setImageResource(android.R.drawable.ic_media_pause);
                }

                prefs.isTrackingRunning().put(!prefs.isTrackingRunning().get());
                checkDrawOverlayPermission();

            }
        });

        if (prefs.isTrackingRunning().get()) {
            fab.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            fab.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        /** check if received result code
         is equal our requested code for draw permission  */
        if (requestCode == REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                // continue here - permission was granted
                loadViews();
            }
        }
    }
}

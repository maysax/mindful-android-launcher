package minium.co.launcher2;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.input.InputManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.ViewById;

import minium.co.core.log.LogConfig;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.launcher2.battery.BatteryChangeReceiver_;
import minium.co.launcher2.ui.TopFragment;
import minium.co.launcher2.ui.TopFragment_;

@Fullscreen
@EActivity(R.layout.activity_main)
public class MainActivity extends CoreActivity {

    private final String TRACE_TAG = LogConfig.TRACE_TAG + "MainActivity";

    @Trace(tag = TRACE_TAG)
    @AfterViews
    void afterViews() {
        loadTopView();
        loadSearchView();
        loadMainView();
        loadBottomview();
    }

    // TODO: try to move this inside TopFragment
    private BroadcastReceiver mBatteryInfoReceiver = new BatteryChangeReceiver_();

    @Override
    protected void onStart() {
        super.onStart();
        this.registerReceiver(this.mBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(this.mBatteryInfoReceiver);
    }

    void loadTopView() {
        loadFragment(TopFragment_.builder().build(), R.id.statusView);
    }

    void loadSearchView() {

    }

    void loadMainView() {

    }

    void loadBottomview() {

    }
}

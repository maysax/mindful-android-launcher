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
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.ViewById;

import de.greenrobot.event.Subscribe;
import minium.co.core.log.LogConfig;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.launcher2.battery.BatteryChangeReceiver_;
import minium.co.launcher2.events.LoadFragmentEvent;
import minium.co.launcher2.events.SearchTextChangedEvent;
import minium.co.launcher2.helper.SearchTextParser;
import minium.co.launcher2.ui.ContactsPickerFragment;
import minium.co.launcher2.ui.ContactsPickerFragment_;
import minium.co.launcher2.ui.MainFragment_;
import minium.co.launcher2.ui.SearchFragment_;
import minium.co.launcher2.ui.TopFragment;
import minium.co.launcher2.ui.TopFragment_;

@Fullscreen
@EActivity(R.layout.activity_main)
public class MainActivity extends CoreActivity {

    private final String TRACE_TAG = LogConfig.TRACE_TAG + "MainActivity";

    @Bean
    SearchTextParser searchTextParser;

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
        loadFragment(SearchFragment_.builder().build(), R.id.searchView);
    }

    void loadMainView() {
        loadFragment(MainFragment_.builder().build(), R.id.mainView);
    }

    void loadBottomview() {

    }

    @Trace(tag = TRACE_TAG)
    @Subscribe
    public void onEvent(SearchTextChangedEvent event) {
        searchTextParser.onTextChanged(event);
    }

    @Subscribe
    public void onEvent(LoadFragmentEvent event) {
        if (event.getId() == LoadFragmentEvent.CONTACTS_LIST)
            loadFragment(ContactsPickerFragment_.builder().build(), R.id.mainView);
        else if (event.getId() == LoadFragmentEvent.MAIN_FRAGMENT)
            loadMainView();
    }
}

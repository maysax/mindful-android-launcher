package minium.co.launcher2;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.Trace;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.log.LogConfig;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;
import minium.co.launcher2.battery.BatteryChangeReceiver_;
import minium.co.launcher2.contactspicker.ContactDetailsFragment;
import minium.co.launcher2.contactspicker.ContactDetailsFragment_;
import minium.co.launcher2.contactspicker.ContactsPickerFragment_;
import minium.co.launcher2.contactspicker.OnContactSelectedListener;
import minium.co.launcher2.events.LoadFragmentEvent;
import minium.co.launcher2.events.MakeChipEvent;
import minium.co.launcher2.events.SearchTextChangedEvent;
import minium.co.launcher2.helper.SearchTextParser;
import minium.co.launcher2.messages.SmsObserver;
import minium.co.launcher2.ui.EnterMessageFragment;
import minium.co.launcher2.ui.EnterMessageFragment_;
import minium.co.launcher2.ui.MainFragment_;
import minium.co.launcher2.ui.SearchFragment_;
import minium.co.launcher2.ui.TopFragment_;

@Fullscreen
@EActivity(R.layout.activity_main)
public class MainActivity extends CoreActivity implements OnContactSelectedListener, SmsObserver.OnSmsSentListener {

    private final String TRACE_TAG = LogConfig.TRACE_TAG + "MainActivity";

    public static int SELECTED_OPTION;

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

    @Override
    public void onContactNameSelected(long contactId, String contactName) {
        EventBus.getDefault().post(new MakeChipEvent(0, 0, contactName));
        loadFragment(ContactDetailsFragment_.builder().selectedContactId(contactId).build(), R.id.mainView);
    }

    @Override
    public void onContactNumberSelected(String contactNumber, String contactName) {
        UIUtils.toast(this, "Number: " + contactName);
        EventBus.getDefault().post(new MakeChipEvent(0, 0, contactName));
        if (SELECTED_OPTION == 1)
            loadFragment(EnterMessageFragment_.builder().phoneNumber(contactNumber).build(), R.id.mainView);
        else if (SELECTED_OPTION == 2) {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contactNumber)));
        }
    }

    @Override
    public void onSmsSent(int threadId) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("minium.co.messages", "com.moez.QKSMS.ui.MainActivity_"));
        intent.putExtra("thread_id", Long.valueOf(threadId));
        try {
            startActivity(intent);
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
            UIUtils.alert(this, "Minium-messages app not found.");
        }
    }
}

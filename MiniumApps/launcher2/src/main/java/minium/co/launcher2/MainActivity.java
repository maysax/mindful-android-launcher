package minium.co.launcher2;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.ViewById;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.log.LogConfig;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.ThemeUtils;
import minium.co.core.util.UIUtils;
import minium.co.launcher2.battery.BatteryChangeReceiver_;
import minium.co.launcher2.calllog.CallLogFragment_;
import minium.co.launcher2.contactspicker.ContactsPickerFragment_;
import minium.co.launcher2.contactspicker.OnContactSelectedListener;
import minium.co.launcher2.data.ActionItemManager;
import minium.co.launcher2.events.ActionItemUpdateEvent;
import minium.co.launcher2.events.LoadFragmentEvent;
import minium.co.launcher2.filter.FilterFragment_;
import minium.co.launcher2.flow.FlowActivity_;
import minium.co.launcher2.helper.SearchTextParser;
import minium.co.launcher2.messages.SmsObserver;
import minium.co.launcher2.model.ActionItem;
import minium.co.launcher2.ui.SearchFragment_;
import minium.co.launcher2.ui.TopFragment_;

@Fullscreen
@EActivity(R.layout.activity_main)
public class MainActivity extends CoreActivity implements OnContactSelectedListener, SmsObserver.OnSmsSentListener {

    @ViewById
    FrameLayout statusView;

    @ViewById
    FrameLayout searchView;

    private final String TRACE_TAG = LogConfig.TRACE_TAG + "MainActivity";

    private int loadedFragmentId = -1;

    @Bean
    SearchTextParser searchTextParser;

    @Bean
    ActionItemManager manager;

    boolean isDispatched = false;

    @Trace(tag = TRACE_TAG)
    @AfterViews
    void afterViews() {
        statusView.setBackgroundColor(ThemeUtils.getPrimaryDarkColor(this));
        searchView.setBackgroundColor(ThemeUtils.getPrimaryDarkColor(this));

        loadTopView();
        loadSearchView();
        loadMainView();
        loadBottomView();
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

    @Override
    protected void onResume() {
        super.onResume();
        isDispatched = false;
    }

    void loadTopView() {
        loadFragment(TopFragment_.builder().build(), R.id.statusView);
    }

    void loadSearchView() {
        loadFragment(SearchFragment_.builder().build(), R.id.searchView);
    }

    void loadMainView() {
//        loadFragment(MainFragment_.builder().build(), R.id.mainView);
        loadFragment(FilterFragment_.builder().build(), R.id.mainView);
    }

    void loadBottomView() {

    }

    @Subscribe
    public void onEvent(LoadFragmentEvent event) {
        if (loadedFragmentId == event.getId()) return;
        if (event.getId() == LoadFragmentEvent.CONTACTS_LIST)
            loadFragment(ContactsPickerFragment_.builder().build(), R.id.mainView);
        else if (event.getId() == LoadFragmentEvent.MAIN_FRAGMENT)
            loadMainView();
        else if (event.getId() == LoadFragmentEvent.CALL_LOG)
            loadFragment(CallLogFragment_.builder().build(), R.id.mainView);

        loadedFragmentId = event.getId();
    }

    @Override
    public void onContactNameSelected(long contactId, String contactName) {
//        EventBus.getDefault().post(new MakeChipEvent(0, 0, contactName));
//        loadFragment(ContactDetailsFragment_.builder().selectedContactId(contactId).build(), R.id.mainView);
        manager.getCurrent().setActionText(contactName);
        manager.fireEvent();
        onEvent(new LoadFragmentEvent(LoadFragmentEvent.CONTACTS_NUMBER_LIST));
    }

    @Override
    public void onContactNumberSelected(String contactNumber, String contactName) {
        UIUtils.toast(this, "Number: " + contactName);
        manager.getCurrent().setActionText(contactName).setExtra(contactNumber).setCompleted(true);
        manager.fireEvent();
        onEvent(new LoadFragmentEvent(LoadFragmentEvent.SEND));
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

    @Override
    public void onBackPressed() {
        EventBus.getDefault().post(new LoadFragmentEvent(LoadFragmentEvent.MAIN_FRAGMENT));
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (!isDispatched) {
                    FlowActivity_.intent(this).isVolumeUpInit(true).start();
                    isDispatched = true;
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    @Subscribe
    public void onEvent(ActionItemUpdateEvent event) {
        ActionItem current = manager.getCurrent();
        if (current == ActionItem.CONTACT) {
            onEvent(new LoadFragmentEvent(LoadFragmentEvent.CONTACTS_LIST));
        }
//        else if (current == ActionItem.TEXT || current == ActionItem.CALL) {
//            manager.add(ActionItem.CONTACT);
//            loadFragment(ContactsPickerFragment_.builder().build());
//        } else if (current == ActionItem.EMPTY) {
//            loadFragment(FilterFragment_.builder().build());
//        }
    }
}

package minium.co.launcher2;

import android.Manifest;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.DroidPrefs;
import minium.co.core.log.LogConfig;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.ServiceUtils;
import minium.co.core.util.ThemeUtils;
import minium.co.core.util.UIUtils;
import minium.co.launcher2.battery.BatteryChangeReceiver_;
import minium.co.launcher2.calllog.CallLogFragment_;
import minium.co.launcher2.contactspicker.ContactDetailsFragment_;
import minium.co.launcher2.contactspicker.ContactsPickerFragment2_;
import minium.co.launcher2.contactspicker.OnContactSelectedListener;
import minium.co.launcher2.data.ActionItemManager;
import minium.co.launcher2.events.ActionItemUpdateEvent;
import minium.co.launcher2.events.BackPressEvent;
import minium.co.launcher2.events.LoadFragmentEvent;
import minium.co.launcher2.filter.FilterFragment_;
import minium.co.launcher2.filter.OptionsFragment_;
import minium.co.launcher2.flow.FlowActivity_;
import minium.co.launcher2.flow.SiempoNotificationService;
import minium.co.launcher2.flow.SiempoNotificationService_;
import minium.co.launcher2.helper.ActionRouter;
import minium.co.launcher2.intro.SiempoIntroActivity;
import minium.co.launcher2.intro.SiempoIntroActivity_;
import minium.co.launcher2.messages.SmsObserver;
import minium.co.launcher2.model.ActionItem;
import minium.co.launcher2.nfc.NfcManager;
import minium.co.launcher2.notificationscheduler.NotificationSchedulerFragment_;
import minium.co.launcher2.ui.ContextualOptionFragment_;
import minium.co.launcher2.ui.OptionsFragment2_;
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
    private long contactId;

    @Bean
    ActionItemManager manager;

    @Bean
    ActionRouter router;

    NfcManager nfcManager = new NfcManager();

    boolean isDispatched = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!prefs.hasShownIntroScreen().get()) {
            SiempoIntroActivity_.intent(this).start();
            finish();
        }

        nfcManager.init(this);
    }

    @Trace(tag = TRACE_TAG)
    @AfterViews
    void afterViews() {
        statusView.setBackgroundColor(ThemeUtils.getPrimaryDarkColor(this));
        searchView.setBackgroundColor(ThemeUtils.getPrimaryDarkColor(this));
        if (prefs.hasShownIntroScreen().get()) {
            new TedPermission(this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("If you reject permission, app can not provide you the seamless integration.\n\nPlease consider turn on permissions at Setting > Permission")
                    .setPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG)
                    .check();

            if (!isEnabled(this)) {
                UIUtils.confirm(this, "Ebb flow manager service is not enabled. Please allow Ebb flow manager to access notification service", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                    }
                });
            }
        }
        loadViews();
    }

    /** @return True if {@link SiempoNotificationService} is enabled. */
    public static boolean isEnabled(Context mContext) {
        return ServiceUtils.isNotificationListenerServiceRunning(mContext, SiempoNotificationService_.class);
    }

    void loadViews() {
        loadTopView();
        loadSearchView();
//        loadMainView();
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
        nfcManager.onResume();
        isDispatched = false;
    }

    void loadTopView() {
        loadFragment(TopFragment_.builder().build(), R.id.statusView, "status");
    }

    void loadSearchView() {
        loadFragment(SearchFragment_.builder().build(), R.id.searchView, "search");
    }

    void loadMainView() {
//        loadFragment(MainFragment_.builder().build(), R.id.mainView);
        loadFragment(FilterFragment_.builder().build(), R.id.mainView, "main");
    }

    void loadBottomView() {

    }

//    void resetView() {
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction t = fragmentManager.beginTransaction();
//            Fragment f = fragmentManager.findFragmentByTag("body");
//            if(f!=null) t.remove(f);
//
//        t.commitAllowingStateLoss();
//    }

    @Subscribe
    public void onEvent(LoadFragmentEvent event) {
        if (loadedFragmentId == event.getId()) {
            return;
        }

        switch (event.getId()) {
            case LoadFragmentEvent.CONTACTS_LIST:
                loadFragment(ContactsPickerFragment2_.builder().build(), R.id.mainView, "main");
                break;
            case LoadFragmentEvent.CONTACTS_NUMBER_LIST:
                loadFragment(ContactDetailsFragment_.builder().selectedContactId(contactId).contactName(manager.getPrevious()
                        .getActionText()).build(), R.id.mainView, "main");
                break;
            case LoadFragmentEvent.CONTEXTUAL_OPTIONS:
                loadFragment(ContextualOptionFragment_.builder().build(), R.id.mainView, "main");
                break;
            default:
            case LoadFragmentEvent.MAIN_FRAGMENT:
                loadMainView();
                break;
            case LoadFragmentEvent.CALL_LOG:
                loadChildFragment(CallLogFragment_.builder().build(), R.id.bodyView);
                break;
            case LoadFragmentEvent.OPTIONS:
                loadFragment(OptionsFragment_.builder().build(), R.id.mainView, "main");
                break;
            case LoadFragmentEvent.OPTIONS_2:
                loadFragment(OptionsFragment2_.builder().build(), R.id.mainView, "main");
                break;
            case LoadFragmentEvent.NOTIFICATION_SCHEDULER:
                loadChildFragment(NotificationSchedulerFragment_.builder().build(), R.id.bodyView);
                break;
        }

        loadedFragmentId = event.getId();
    }

    @Override
    public void onContactNameSelected(long contactId, String contactName) {
        this.contactId = contactId;
        manager.setCurrent(new ActionItem(ActionItem.ActionItemType.CONTACT));
        manager.getCurrent().setExtra(String.valueOf(contactId)).setActionText(contactName).setCompleted(true);
        manager.fireEvent();
    }

    @Override
    public void onContactNumberSelected(long contactId, String contactName, String contactNumber) {
        UIUtils.toast(this, "Selected " + contactName + " (" + contactNumber + ")");
        if (manager.getCurrent().getType() == ActionItem.ActionItemType.CONTACT) {
            manager.getCurrent().setActionText(contactName).setExtra(String.valueOf(contactId)).setCompleted(true);
            manager.add(new ActionItem(ActionItem.ActionItemType.CONTACT_NUMBER));
        } else if (manager.getCurrent().getType() == ActionItem.ActionItemType.CONTACT_NUMBER){
            manager.getPrevious().setActionText(contactName).setExtra(String.valueOf(contactId)).setCompleted(true);
        } else {
            manager.setCurrent(new ActionItem(ActionItem.ActionItemType.CONTACT));
            manager.getCurrent().setActionText(contactName).setExtra(String.valueOf(contactId)).setCompleted(true);
            manager.add(new ActionItem(ActionItem.ActionItemType.CONTACT_NUMBER));
        }
        manager.getCurrent().setActionText(contactNumber).setExtra(contactName).setCompleted(true);
        manager.fireEvent();
    }

    @Override
    public void onSmsSent(int threadId) {
        Intent defineIntent = new Intent(Intent.ACTION_VIEW);
        defineIntent.setData(Uri.parse("content://mms-sms/conversations/"+threadId));
        try {
            startActivity(defineIntent);
            manager.clear();
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
            UIUtils.alert(this, "Minium-messages app not found.");
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager.getBackStackEntryCount() == 1) {
            loadedFragmentId = LoadFragmentEvent.MAIN_FRAGMENT;
            loadMainView();
        } else {
            EventBus.getDefault().post(new BackPressEvent());
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Tracer.d("Keycode any pressed " + event.getKeyCode());
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                Tracer.d("Keycode volume up pressed");
                if (!isDispatched) {
                    FlowActivity_.intent(this).start();
                    isDispatched = true;
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    @Subscribe
    public void onEvent(ActionItemUpdateEvent event) {
        router.onActionItemUpdate(this, manager);
    }


    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {

        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            UIUtils.toast(MainActivity.this, "Permission denied");
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        nfcManager.onNewIntent(intent);
    }
}

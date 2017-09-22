package co.siempo.phone.settings;

import android.app.Fragment;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.model.SettingsData;
import co.siempo.phone.notification.NotificationFragment;
import co.siempo.phone.notification.NotificationRetreat_;
import co.siempo.phone.notification.StatusBarHandler;
import co.siempo.phone.ui.TopFragment_;
import de.greenrobot.event.Subscribe;
import minium.co.core.event.HomePressEvent;
import minium.co.core.ui.CoreActivity;


@Fullscreen
@EActivity(R.layout.activity_settings_main)
public class SiempoMainSettingsActivity extends CoreActivity {

    private ListView lst_settings;
    private ArrayList<SettingsData> arr_menuList;
    private SettingsAdapter adapter;
    private StatusBarHandler statusBarHandler;
    private Context context;
    private final String TAG="SiempoMainSetting";
    private ActivityState state;
    /**
     * Activitystate is use to identify state whether the screen is coming from
     * after homepress event or from normal flow.
     */
    private enum ActivityState {
        NORMAL,
        ONHOMEPRESS
    }


    @AfterViews
    void afterViews() {
        initView();
        onClickEvents();
        loadTopBar();
        loadStatusBar();
    }

    /**
     *  Below snippet is use to first check if siempo status bar is restricted from another activity,
     *  then it first remove siempo status bar and restrict siempo status bar with reference to this activity
     */
    synchronized void loadStatusBar() {
        try {
            statusBarHandler = new StatusBarHandler(SiempoMainSettingsActivity.this);
            NotificationRetreat_.getInstance_(this.getApplicationContext()).retreat();
            if (statusBarHandler != null) {
                statusBarHandler.restoreStatusBarExpansion();
            }

            if(statusBarHandler!=null && !statusBarHandler.isActive()) {
                statusBarHandler.requestStatusBarCustomization();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickEvents() {
        lst_settings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SettingsData settingsData = (SettingsData) lst_settings.getItemAtPosition(position);
                if (settingsData != null) {
                    switch (settingsData.getId()) {
                        case 1:
                            restoreSiempoNotificationBar();
                            new ActivityHelper(context).openPhoneSettingsApp();
                            break;
                        case 2:
                            Launcher3App.getInstance().setSiempoBarLaunch(false);
                            new ActivityHelper(context).openSiempoSettingsApp();
                            break;
                        case 3:
                            Launcher3App.getInstance().setSiempoBarLaunch(false);
                            new ActivityHelper(context).openSiempoAlphaSettingsApp();
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    public void initView() {
        Launcher3App.getInstance().setSiempoBarLaunch(true);
        context = SiempoMainSettingsActivity.this;
        lst_settings = (ListView) findViewById(R.id.lst_settings);


        arr_menuList = new ArrayList<>();
        SettingsData s1 = new SettingsData();
        s1.setSettingType(getString(R.string.str_phonesettings));
        s1.setId(1);
        arr_menuList.add(s1);


        SettingsData s2 = new SettingsData();
        s2.setSettingType(getString(R.string.str_siemposettings));
        s2.setId(2);
        arr_menuList.add(s2);

        SettingsData s3 = new SettingsData();
        s3.setSettingType(getString(R.string.str_siempo_alphasettings));
        s3.setId(3);
        arr_menuList.add(s3);


        adapter = new SettingsAdapter(this, arr_menuList);
        lst_settings.setAdapter(adapter);
        loadTopBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * Below snippet is use to load siempo status bar when launch from background.
         */
        if(state== ActivityState.ONHOMEPRESS){
            if(statusBarHandler!=null && !statusBarHandler.isActive()) {
                statusBarHandler.requestStatusBarCustomization();
            }
        }
        // If status bar view becomes null,reload the statusbar
        if (getSupportFragmentManager().findFragmentById(R.id.statusView) == null) {
            loadTopBar();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        Launcher3App.getInstance().setSiempoBarLaunch(false);
        if (statusBarHandler!=null && statusBarHandler.isNotificationTrayVisible) {
            /**
             *  Below snippet is use to remove notification fragment (Siempo Notification Screen) if visible on screen
             */
            Fragment f = getFragmentManager().findFragmentById(R.id.mainView);
            if(f == null){
                Log.d(TAG," Fragment is null");
                super.onBackPressed();
            }
            else if (f!=null && f instanceof NotificationFragment && f.isAdded())
            {
                statusBarHandler.isNotificationTrayVisible = false;
                ((NotificationFragment) f).animateOut();
                super.onBackPressed();
            }
            else{
                super.onBackPressed();
            }
        }
        else{
            super.onBackPressed();
        }
    }
    private void loadTopBar() {
        loadFragment(TopFragment_.builder().build(), R.id.statusView, "status");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Launcher3App.getInstance().setSiempoBarLaunch(true);
        loadStatusBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentIndex=0;
    }



    public void restoreSiempoNotificationBar(){
        try {
            statusBarHandler = new StatusBarHandler(SiempoMainSettingsActivity.this);
            NotificationRetreat_.getInstance_(this.getApplicationContext()).retreat();
            if (statusBarHandler != null) {
                statusBarHandler.restoreStatusBarExpansion();
            }

            if (StatusBarHandler.isNotificationTrayVisible) {
                Fragment f = getFragmentManager().findFragmentById(R.id.mainView);
                if (f == null) {
                    Log.d(TAG, "Fragment is null");
                } else if (f != null && f.isAdded() && f instanceof NotificationFragment) {
                    StatusBarHandler.isNotificationTrayVisible = false;
                    ((NotificationFragment) f).animateOut();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @SuppressWarnings("ConstantConditions")
    @Subscribe
    public void homePressEvent(HomePressEvent event) {
        state= ActivityState.ONHOMEPRESS;
        if (event.isVisible()) {
            /**
             *  Below snippet is use to remove notification fragment (Siempo Notification Screen) if visible on screen
             */
            if (StatusBarHandler.isNotificationTrayVisible) {

                Fragment f = getFragmentManager().findFragmentById(R.id.mainView);
                if(f == null){
                    Log.d(TAG,"F is null");
                }
                else if (f!=null && f.isAdded() && f instanceof NotificationFragment)
                {
                    StatusBarHandler.isNotificationTrayVisible = false;
                    ((NotificationFragment) f).animateOut();
                }
            }
            /**
             *  Below snippet is use to remove siempo status bar
             */
            if(statusBarHandler!=null){
                NotificationRetreat_.getInstance_(this.getApplicationContext()).retreat();
                try{
                    statusBarHandler.restoreStatusBarExpansion();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Launcher3App.getInstance().setSiempoBarLaunch(true);
        if(state== ActivityState.ONHOMEPRESS){
            state= ActivityState.NORMAL;
        }
    }
}

package co.siempo.phone.applist;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.notification.NotificationFragment;
import co.siempo.phone.notification.NotificationRetreat_;
import co.siempo.phone.notification.StatusBarHandler;
import co.siempo.phone.ui.TopFragment_;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.event.HomePressEvent;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;

@Fullscreen
@EActivity(R.layout.activity_installed_app_list)
public class AppDrawerActivity extends CoreActivity {
        //implements LoaderManager.LoaderCallbacks<List<ApplistDataModel>> {

    StatusBarHandler statusBarHandler;

    List<ApplicationInfo> arrayList = new ArrayList<>();
    @ViewById
    GridView activity_grid_view;

    @ViewById
    ImageView crossActionBar;

    @Click
    void crossActionBar() {
        this.finish();
    }

    @ViewById
    TextView titleActionBar;
    @ViewById
    ImageView settingsActionBar;
    InstalledAppListAdapter installedAppListAdapter;
    private String TAG="AppDrawerActivity";


    private ActivityState state;


    /**
     * Activitystate is use to identify state whether the screen is coming from
     * after homepress event or from normal flow.
     */

    private enum ActivityState {
        NORMAL,
        ONHOMEPRESS
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(state== ActivityState.ONHOMEPRESS){
            state= ActivityState.NORMAL;
        }
    }
    @AfterViews
    void afterViews() {

        settingsActionBar.setVisibility(View.INVISIBLE);
        titleActionBar.setText(getString(R.string.title_apps));
        activity_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                try {
                    state=ActivityState.ONHOMEPRESS;
                    restoreSiempoNotificationBar();
                    Tracer.i("Opening package: " + arrayList.get(i).packageName);
                    new ActivityHelper(AppDrawerActivity.this).openGMape(arrayList.get(i).packageName);
                    EventBus.getDefault().post(new AppOpenEvent(arrayList.get(i).packageName));
                } catch (Exception e) {
                    // returns null if application is not installed
                    Tracer.e(e, e.getMessage());
                }
            }
        });

        arrayList = CoreApplication.getInstance().getPackagesList();
        installedAppListAdapter = new InstalledAppListAdapter(AppDrawerActivity.this,arrayList);
        //installedAppListAdapter.setAppInfo(arrayList);
        activity_grid_view.setAdapter(installedAppListAdapter);
       // getLoaderManager().initLoader(0, null, this);
        loadTopBar();
        loadStatusBar();

    }

    /**
     *  Below snippet is use to first check if siempo status bar is restricted from another activity,
     *  then it first remove siempo status bar and restrict siempo status bar with reference to this activity
     */
    synchronized void loadStatusBar() {
        try {
            statusBarHandler = new StatusBarHandler(AppDrawerActivity.this);
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

    @Subscribe
    public void appOpenEvent(AppOpenEvent event) {
        new AppOpenHandler().handle(this, event);
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

    }

    private void loadTopBar() {
        loadFragment(TopFragment_.builder().build(), R.id.statusView, "status");
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
        /**
         *  Below snippet is use to remove notification fragment (Siempo Notification Screen) if visible on screen
         */
        if (statusBarHandler!=null && statusBarHandler.isNotificationTrayVisible) {
            Fragment f = getFragmentManager().findFragmentById(R.id.mainView);
            if(f == null){
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


    @Override
    protected void onRestart() {
        super.onRestart();
        loadStatusBar();
    }


    @SuppressWarnings("ConstantConditions")
    @Subscribe
    public void homePressEvent(HomePressEvent event) {
        Log.d(TAG,"ACTION HOME PRESS");
        state= ActivityState.ONHOMEPRESS;
        if (event.isVisible()) {
            restoreSiempoNotificationBar();
        }
    }

    public void restoreSiempoNotificationBar(){
        /**
         *  Below snippet is use to remove notification fragment (Siempo Notification Screen) if visible on screen
         */
        if (StatusBarHandler.isNotificationTrayVisible) {

            Fragment f = getFragmentManager().findFragmentById(R.id.mainView);
            if(f == null){
                Log.d(TAG,"Fragment is null");
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

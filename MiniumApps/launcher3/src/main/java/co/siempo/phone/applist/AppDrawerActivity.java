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

    @AfterViews
    void afterViews() {

        settingsActionBar.setVisibility(View.INVISIBLE);
        titleActionBar.setText(getString(R.string.title_apps));
        activity_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                try {
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

    @UiThread(delay = 1000)
    void loadStatusBar() {
        statusBarHandler = new StatusBarHandler(AppDrawerActivity.this);
        if(statusBarHandler!=null && !statusBarHandler.isActive()) {
            statusBarHandler.requestStatusBarCustomization();
        }
    }

    @Subscribe
    public void appOpenEvent(AppOpenEvent event) {
        new AppOpenHandler().handle(this, event);
    }

    @Override
    protected void onResume() {
        super.onResume();

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
        NotificationRetreat_.getInstance_(this.getApplicationContext()).retreat();
        try {
            if(statusBarHandler!=null)
                statusBarHandler.restoreStatusBarExpansion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try{
            if (statusBarHandler.isNotificationTrayVisible) {
                Fragment f = getFragmentManager().findFragmentById(R.id.mainView);
                if (f instanceof NotificationFragment) ;
                {
                    statusBarHandler.isNotificationTrayVisible = false;
                    ((NotificationFragment) f).animateOut();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        loadStatusBar();
    }

}

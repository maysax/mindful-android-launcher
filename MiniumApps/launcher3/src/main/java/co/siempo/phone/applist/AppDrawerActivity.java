package co.siempo.phone.applist;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
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
import co.siempo.phone.notification.NotificationFragment;
import co.siempo.phone.notification.NotificationRetreat_;
import co.siempo.phone.notification.StatusBarHandler;
import co.siempo.phone.pause.PauseActivity;
import co.siempo.phone.ui.TopFragment_;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;

@Fullscreen
@EActivity(R.layout.activity_installed_app_list)
public class AppDrawerActivity extends CoreActivity implements LoaderManager.LoaderCallbacks<List<ApplistDataModel>> {

    StatusBarHandler statusBarHandler;

    ArrayList<ApplistDataModel> arrayList = new ArrayList<>();
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
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        //List<ResolveInfo> pkgAppsList = getPackageManager().queryIntentActivities( mainIntent, 0);
        activity_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                try {
                    Tracer.i("Opening package: " + arrayList.get(i).getPackageName());
                    Intent intent = getPackageManager().getLaunchIntentForPackage(arrayList.get(i).getPackageName());
                    startActivity(intent);
                    EventBus.getDefault().post(new AppOpenEvent(arrayList.get(i).getPackageName()));
                } catch (Exception e) {
                    // returns null if application is not installed
                    Tracer.e(e, e.getMessage());
                }
            }
        });

        /*
        PackageManager pm = getPackageManager();
       // List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        //List<ApplicationInfo> installedApps = new ArrayList<>();
        ArrayList<ApplistDataModel> arrayList = new ArrayList<>();
        ApplistDataModel applistDataModel;
        for(ApplicationInfo app : apps) {
            applistDataModel  =new ApplistDataModel();
            //checks for flags; if flagged, check if updated system app
            if((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                //installedApps.add(app);
                //it's a system app, not interested
            } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //Discard this one
                //in this case, it should be a user-installed app
                //installedApps.add(app);
                try{

                }
                String appname = getPackageManager().getApplicationLabel(app).toString();
                Drawable icon =  getPackageManager().getApplicationIcon(app.packageName);
                applistDataModel.setName(appname);
                applistDataModel.setIcon(icon);
                arrayList.add(applistDataModel);
            }
            else {
               // installedApps.add(app);
               // installedApps.add(app);

            }
        }
        */

        installedAppListAdapter = new InstalledAppListAdapter(AppDrawerActivity.this);
        activity_grid_view.setAdapter(installedAppListAdapter);
        getLoaderManager().initLoader(0, null, this);
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
    @Override
    public Loader<List<ApplistDataModel>> onCreateLoader(int i, Bundle bundle) {
        return AppListLoader_.getInstance_(this);
    }

    @Override
    public void onLoadFinished(Loader<List<ApplistDataModel>> loader, List<ApplistDataModel> applistDataModels) {
        arrayList.clear();
        arrayList.addAll(applistDataModels);
        installedAppListAdapter.setAppInfo(applistDataModels);
    }

    @Override
    public void onLoaderReset(Loader<List<ApplistDataModel>> loader) {
        installedAppListAdapter.setAppInfo(null);
    }

    @Subscribe
    public void appOpenEvent(AppOpenEvent event) {
        new AppOpenHandler().handle(this, event);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (statusBarHandler != null && !statusBarHandler.isActive())
            statusBarHandler.requestStatusBarCustomization();
    }

    private void loadTopBar() {
        loadFragment(TopFragment_.builder().build(), R.id.statusView, "status");
    }


    @Override
    protected void onPause() {
        super.onPause();
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
            Log.d(TAG,"Exception onBackPressed :: "+e.toString());
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        if(statusBarHandler!=null){
            statusBarHandler = new StatusBarHandler(this);
        }
    }
}

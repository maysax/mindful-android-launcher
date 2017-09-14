package co.siempo.phone.applist;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.joanzapata.iconify.IconDrawable;

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

    StatusBarHandler statusBarHandler;

    List<ApplicationInfo> arrayList = new ArrayList<>();

    @ViewById
    RecyclerView activity_grid_view;

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

    @ViewById
    ImageView btnListOrGrid;

    InstalledAppListAdapter installedAppListAdapter;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String TAG="AppDrawerActivity";

    @AfterViews
    void afterViews() {

        settingsActionBar.setVisibility(View.GONE);
        titleActionBar.setText(getString(R.string.title_apps));
        arrayList = CoreApplication.getInstance().getPackagesList();
        btnListOrGrid.setImageDrawable(new IconDrawable(AppDrawerActivity.this, "fa-th")
                .colorRes(R.color.text_primary)
                .sizeDp(20));
        btnListOrGrid.setVisibility(View.VISIBLE);
        mLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        activity_grid_view.setLayoutManager(mLayoutManager);
        mAdapter = new InstalledAppListAdapter(AppDrawerActivity.this,arrayList,true);
        activity_grid_view.setAdapter(mAdapter);

        loadTopBar();
        loadStatusBar();
        // Listener for the grid and list icon.
        btnListOrGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnListOrGrid.getTag().toString().equalsIgnoreCase("1")){
                    btnListOrGrid.setTag("0");
                    btnListOrGrid.setImageDrawable(new IconDrawable(AppDrawerActivity.this, "fa-th")
                            .colorRes(R.color.text_primary)
                            .sizeDp(20));
                    mLayoutManager = new GridLayoutManager(getApplicationContext(),3);
                    activity_grid_view.setLayoutManager(mLayoutManager);
                    mAdapter = new InstalledAppListAdapter(AppDrawerActivity.this,arrayList,true);
                    activity_grid_view.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }else{
                    btnListOrGrid.setTag("1");
                    btnListOrGrid.setImageDrawable(new IconDrawable(AppDrawerActivity.this, "fa-list")
                            .colorRes(R.color.text_primary)
                            .sizeDp(20));
                    mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    activity_grid_view.setLayoutManager(mLayoutManager);
                    mAdapter = new InstalledAppListAdapter(AppDrawerActivity.this,arrayList,false);
                    activity_grid_view.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

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

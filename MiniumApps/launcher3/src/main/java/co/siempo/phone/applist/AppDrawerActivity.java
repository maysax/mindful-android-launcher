package co.siempo.phone.applist;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.notification.NotificationFragment;
import co.siempo.phone.notification.NotificationRetreat_;
import co.siempo.phone.ui.TopFragment_;
import co.siempo.phone.util.PackageUtil;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.event.AppInstalledEvent;
import minium.co.core.event.HomePressEvent;
import minium.co.core.ui.CoreActivity;

@EActivity(R.layout.activity_installed_app_list)
public class AppDrawerActivity extends CoreActivity {


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
    ProgressDialog progressDialog;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @AfterViews
    void afterViews() {
        settingsActionBar.setVisibility(View.GONE);
        titleActionBar.setText(getString(R.string.title_apps));
        arrayList = CoreApplication.getInstance().getPackagesList();

        if (prefs.isGrid().get()) {
            bindAsGrid();
        } else {
            bindAsList();
        }

        // Listener for the grid and list icon.
        btnListOrGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnListOrGrid.getTag().toString().equalsIgnoreCase("1")) {
                    bindAsGrid();
                } else {
                    bindAsList();
                }
            }
        });

    }

    /**
     * Bind View As Listing View.
     */
    private void bindAsList() {
        btnListOrGrid.setTag("1");
        btnListOrGrid.setImageDrawable(new IconDrawable(AppDrawerActivity.this, "fa-th")
                .colorRes(R.color.text_primary)
                .sizeDp(20));
        btnListOrGrid.setVisibility(View.VISIBLE);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        activity_grid_view.setLayoutManager(mLayoutManager);
        mAdapter = new InstalledAppListAdapter(AppDrawerActivity.this, arrayList, false);
        activity_grid_view.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        prefs.isGrid().put(false);

    }

    /**
     * Bind View As Grid View.
     */
    private void bindAsGrid() {
        btnListOrGrid.setImageDrawable(new IconDrawable(AppDrawerActivity.this, "fa-list")
                .colorRes(R.color.text_primary)
                .sizeDp(20));
        btnListOrGrid.setTag("0");
        btnListOrGrid.setVisibility(View.VISIBLE);
        mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        activity_grid_view.setLayoutManager(mLayoutManager);
        mAdapter = new InstalledAppListAdapter(AppDrawerActivity.this, arrayList, true);
        activity_grid_view.setAdapter(mAdapter);
        prefs.isGrid().put(true);
    }



    @Subscribe
    public void appOpenEvent(AppOpenEvent event) {
        new AppOpenHandler().handle(this, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PackageUtil.checkPermission(this);
        if (prefs.isAppUpdated().get()) {
            progressDialog = ProgressDialog.show(this, "", "Loading....");
            Log.d("Testing","Loading");
            CoreApplication.getInstance().getAllApplicationPackageName();
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

    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event.isRunning()) {
            if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
            arrayList = CoreApplication.getInstance().getPackagesList();
            prefs.isAppUpdated().put(false);
            if (prefs.isGrid().get()) {
                bindAsGrid();
            } else {
                bindAsList();
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @SuppressWarnings("ConstantConditions")
    @Subscribe
    public void homePressEvent(HomePressEvent event) {
        Log.d(TAG,"ACTION HOME PRESS");
        if (event.isVisible()) {
        }
    }

}

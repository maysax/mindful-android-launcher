package co.siempo.phone.applist;

import android.app.Fragment;
import android.content.pm.ApplicationInfo;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
import co.siempo.phone.notification.StatusBarHandler;
import co.siempo.phone.ui.TopFragment_;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.event.HomePressEvent;
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
        Launcher3App.getInstance().setSiempoBarLaunch(false);
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
    private String TAG = "AppDrawerActivity";
    public ActivityState state;

    /**
     * Activitystate is use to identify state whether the screen is coming from
     * after homepress event or from normal flow.
     */

    public enum ActivityState {
        NORMAL,
        ONHOMEPRESS
    }


    @Override
    protected void onStart() {
        super.onStart();
        Launcher3App.getInstance().setSiempoBarLaunch(true);
        if (state == ActivityState.ONHOMEPRESS) {
            state = ActivityState.NORMAL;
        }
    }

    @AfterViews
    void afterViews() {
        Launcher3App.getInstance().setSiempoBarLaunch(true);
        settingsActionBar.setVisibility(View.GONE);
        titleActionBar.setText(getString(R.string.title_apps));
        arrayList = CoreApplication.getInstance().getPackagesList();

        if (prefs.isGrid().get()) {
            bindAsGrid();
        } else {
            bindAsList();
        }

        loadTopBar();
        loadStatusBar();
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

    /**
     * Below snippet is use to first check if siempo status bar is restricted from another activity,
     * then it first remove siempo status bar and restrict siempo status bar with reference to this activity
     */
    synchronized void loadStatusBar() {
        try {
            statusBarHandler = new StatusBarHandler(AppDrawerActivity.this);
            NotificationRetreat_.getInstance_(this.getApplicationContext()).retreat();
            if (statusBarHandler != null) {
                statusBarHandler.restoreStatusBarExpansion();
            }

            if (statusBarHandler != null && !statusBarHandler.isActive()) {
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
        if (state == ActivityState.ONHOMEPRESS) {
            if (statusBarHandler != null && !statusBarHandler.isActive()) {
                statusBarHandler.requestStatusBarCustomization();
            }
        }

        // If status bar view becomes null,reload the statusbar
        if (getSupportFragmentManager().findFragmentById(R.id.statusView) == null) {
            loadTopBar();
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
        Launcher3App.getInstance().setSiempoBarLaunch(false);
        if (statusBarHandler != null && statusBarHandler.isNotificationTrayVisible) {
            Fragment f = getFragmentManager().findFragmentById(R.id.mainView);
            if (f == null) {
                super.onBackPressed();
            } else if (f != null && f instanceof NotificationFragment && f.isAdded()) {
                statusBarHandler.isNotificationTrayVisible = false;
                ((NotificationFragment) f).animateOut();
                super.onBackPressed();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Launcher3App.getInstance().setSiempoBarLaunch(true);
        loadStatusBar();
    }


    @SuppressWarnings("ConstantConditions")
    @Subscribe
    public void homePressEvent(HomePressEvent event) {
        Log.d(TAG, "ACTION HOME PRESS");
        state = ActivityState.ONHOMEPRESS;
        if (event.isVisible()) {
            restoreSiempoNotificationBar();
        }
    }

    public void restoreSiempoNotificationBar() {
        /**
         *  Below snippet is use to remove notification fragment (Siempo Notification Screen) if visible on screen
         */
        if (StatusBarHandler.isNotificationTrayVisible) {

            Fragment f = getFragmentManager().findFragmentById(R.id.mainView);
            if (f == null) {
                Log.d(TAG, "Fragment is null");
            } else if (f != null && f.isAdded() && f instanceof NotificationFragment) {
                StatusBarHandler.isNotificationTrayVisible = false;
                ((NotificationFragment) f).animateOut();

            }
        }
        /**
         *  Below snippet is use to remove siempo status bar
         */
        if (statusBarHandler != null) {
            NotificationRetreat_.getInstance_(this.getApplicationContext()).retreat();
            try {
                statusBarHandler.restoreStatusBarExpansion();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

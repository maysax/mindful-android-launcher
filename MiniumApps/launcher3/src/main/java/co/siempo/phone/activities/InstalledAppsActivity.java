package co.siempo.phone.activities;

import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.joanzapata.iconify.IconDrawable;

import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.adapters.InstalledAppListAdapter;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.event.AppInstalledEvent;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.utils.PackageUtil;
import co.siempo.phone.utils.PrefSiempo;
import de.greenrobot.event.Subscribe;

public class InstalledAppsActivity extends CoreActivity implements View.OnClickListener {


    public static final int UNINSTALL_APP_REQUEST_CODE = 2;
    private List<ApplicationInfo> arrayList = new ArrayList<>();
    private ImageView imgCancel;
    private TextView txtTitle;
    private ImageView imgSetting;
    private ImageView imgListOrGrid;
    private Toolbar toolbar;
    private RecyclerView recyclerViewApps;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressDialog progressDialog;
    private long startTime;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installed_app_list);
        initView();

        arrayList = CoreApplication.getInstance().getPackagesList();

        if (PrefSiempo.getInstance(this).read(PrefSiempo.IS_GRID, true)) {
            bindAsGrid();
        } else {
            bindAsList();
        }


    }

    /**
     * Bind View As Listing View.
     */
    private void bindAsList() {
        imgListOrGrid.setTag("1");
        imgListOrGrid.setImageDrawable(new IconDrawable(InstalledAppsActivity.this, "fa-th")
                .colorRes(R.color.text_primary)
                .sizeDp(20));
        imgListOrGrid.setVisibility(View.VISIBLE);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewApps.setLayoutManager(mLayoutManager);
        mAdapter = new InstalledAppListAdapter(InstalledAppsActivity.this, arrayList, false);
        recyclerViewApps.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        PrefSiempo.getInstance(this).write(PrefSiempo.IS_GRID, false);

    }

    /**
     * Bind View As Grid View.
     */
    private void bindAsGrid() {
        imgListOrGrid.setImageDrawable(new IconDrawable(InstalledAppsActivity.this, "fa-list")
                .colorRes(R.color.text_primary)
                .sizeDp(20));
        imgListOrGrid.setTag("0");
        imgListOrGrid.setVisibility(View.VISIBLE);
        mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerViewApps.setLayoutManager(mLayoutManager);
        mAdapter = new InstalledAppListAdapter(InstalledAppsActivity.this, arrayList, true);
        recyclerViewApps.setAdapter(mAdapter);
        PrefSiempo.getInstance(this).write(PrefSiempo.IS_GRID, true);
    }


    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
        PackageUtil.checkPermission(this);
        if (prefs.isAppUpdated().get()) {
            progressDialog = ProgressDialog.show(this, "", getString(R.string.loading_msg));
            CoreApplication.getInstance().getAllApplicationPackageName();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelper.getIntance().logScreenUsageTime(InstalledAppsActivity.this.getClass().getSimpleName(), startTime);
    }


    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event != null && event.isRunning()) {
            ((Launcher3App) CoreApplication.getInstance()).setAllDefaultMenusApplication();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            arrayList = CoreApplication.getInstance().getPackagesList();
            prefs.isAppUpdated().put(false);
            if (PrefSiempo.getInstance(this).read(PrefSiempo.IS_GRID, true)) {
                bindAsGrid();
            } else {
                bindAsList();
            }
        }
    }

    private void initView() {
        imgCancel = findViewById(R.id.crossActionBar);
        imgCancel.setOnClickListener(this);
        txtTitle = findViewById(R.id.titleActionBar);
        imgSetting = findViewById(R.id.settingsActionBar);
        imgListOrGrid = findViewById(R.id.btnListOrGrid);
        imgListOrGrid.setOnClickListener(this);
        toolbar = findViewById(R.id.toolbar);
        recyclerViewApps = findViewById(R.id.recyclerViewApps);
        imgSetting.setVisibility(View.GONE);
        txtTitle.setText(getString(R.string.title_apps));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.crossActionBar:
                finish();
                break;
            case R.id.btnListOrGrid:
                if (imgListOrGrid.getTag().toString().equalsIgnoreCase("1")) {
                    bindAsGrid();
                } else {
                    bindAsList();
                }
                break;
            default:
                break;
        }
    }
}

package co.siempo.phone.activities;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import co.siempo.phone.R;
import co.siempo.phone.adapters.viewholder.AppAssignmentAdapter;
import co.siempo.phone.app.Constants;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.event.AppInstalledEvent;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.models.AppMenu;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.Sorting;
import de.greenrobot.event.Subscribe;

public class AppAssignmentActivity extends CoreActivity {

    MainListItem mainListItem;
    MenuItem item_tools;
    //8 Photos
    List<Integer> idList = Arrays.asList(2, 4, 6, 9, 10);
    ArrayList<String> connectedAppsList = new ArrayList<>();
    Set<String> set = new HashSet<>();
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView txtErrorMessage;
    private ArrayList<ResolveInfo> appList = new ArrayList<>();
    private AppAssignmentAdapter appAssignmentAdapter;
    private long startTime = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_assign, menu);
        item_tools = menu.findItem(R.id.item_tools);
        if (item_tools != null && mainListItem != null) {
            item_tools.setIcon(mainListItem.getDrawable());
            item_tools.setTitle(mainListItem.getTitle());
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event.isAppInstalledSuccessfully()) {
            filterList();
            initView();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_assignement);
        mainListItem = (MainListItem) getIntent().getSerializableExtra(Constants.INTENT_MAINLISTITEM);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
        set = PrefSiempo.getInstance(this).read(PrefSiempo.JUNKFOOD_APPS, new HashSet<String>());
        filterList();
        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelper.getInstance().logScreenUsageTime(AppAssignmentActivity.this.getClass().getSimpleName(), startTime);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mainListItem = (MainListItem) savedInstanceState.getSerializable("MainListItem");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("MainListItem", mainListItem);

    }

    private void filterList() {
        appList = new ArrayList<>();
        if (mainListItem != null) {
            if (idList.contains(mainListItem.getId())) {
                Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                List<ResolveInfo> installedPackageList = getPackageManager().queryIntentActivities(mainIntent, 0);
                for (Map.Entry<Integer, AppMenu> app : CoreApplication.getInstance().getToolsSettings().entrySet()) {
                    if (app.getKey() != mainListItem.getId()) {
                        AppMenu appMenu = app.getValue();
                        if (!appMenu.getApplicationName().equalsIgnoreCase("")) {
                            connectedAppsList.add(appMenu.getApplicationName());
                        }
                    }
                }
                for (ResolveInfo resolveInfo : installedPackageList) {
                    if (!resolveInfo.activityInfo.packageName.equalsIgnoreCase(getPackageName())) {
                        if (!connectedAppsList.contains(resolveInfo.activityInfo.packageName)) {
                            appList.add(resolveInfo);
                        }
                    }
                }
            } else {
                appList = CoreApplication.getInstance().getApplicationByCategory(mainListItem.getId());
            }
        }
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle(getString(R.string.assign_an_app) + " " + mainListItem.getTitle());
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color
                .colorAccent));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        txtErrorMessage = findViewById(R.id.txtErrorMessage);
        if (appList != null && appList.size() >= 1) {
            recyclerView.setVisibility(View.VISIBLE);
            txtErrorMessage.setVisibility(View.INVISIBLE);
            appList = Sorting.sortAppAssignment(this, appList);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(
                    new DividerItemDecoration(this, mLayoutManager.getOrientation()));
            if (mainListItem != null) {
                appAssignmentAdapter = new AppAssignmentAdapter(this, mainListItem.getId(), appList);
                recyclerView.setAdapter(appAssignmentAdapter);
            }
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            txtErrorMessage.setVisibility(View.VISIBLE);
            if (mainListItem != null) {
                txtErrorMessage.setText("No " + mainListItem.getTitle() + " apps are installed.");
            }
        }

    }


}

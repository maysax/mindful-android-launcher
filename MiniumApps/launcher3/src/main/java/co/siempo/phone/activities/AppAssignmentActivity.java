package co.siempo.phone.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import co.siempo.phone.R;
import co.siempo.phone.adapters.viewholder.AppAssignmentAdapter;
import co.siempo.phone.app.Constants;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.event.AppInstalledEvent;
import co.siempo.phone.event.NotifySearchRefresh;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.models.MainListItem;
import co.siempo.phone.utils.PrefSiempo;
import co.siempo.phone.utils.Sorting;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class AppAssignmentActivity extends CoreActivity {

    public ArrayList<ResolveInfo> appList = new ArrayList<>();
    MainListItem mainListItem;
    MenuItem item_tools;
    //8 Photos
    List<Integer> idList = Arrays.asList(2, 4, 6, 9, 10, 12, 18, 19, 20);
    ArrayList<String> connectedAppsList = new ArrayList<>();
    Set<String> set = new HashSet<>();
    ArrayList<ResolveInfo> appListAll = new ArrayList<>();
    ArrayList<ResolveInfo> mimeList = new ArrayList<>();
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private TextView txtErrorMessage;
    private AppAssignmentAdapter appAssignmentAdapter;
    private TextView showallAppBtn;
    private long startTime = 0;
    private CardView cardView;
    private ImageView imgClear;
    private EditText edtSearch;
    private String class_name;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        class_name = getIntent().getStringExtra("class_name");
        if (mainListItem != null) {
            set = PrefSiempo.getInstance(this).read(PrefSiempo.JUNKFOOD_APPS, new HashSet<String>());
        } else {
            finish();
        }
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
        //Added to refresh if app is marked as non-junk by navigating to Flag
        // Junk Apps directly from this screen
        filterList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().postSticky(new NotifySearchRefresh(true));
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
            List<ResolveInfo> installedPackageList;
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            installedPackageList = getPackageManager().queryIntentActivities(mainIntent, 0);
            if (idList.contains(mainListItem.getId())) {
                /*for (Map.Entry<Integer, AppMenu> app : CoreApplication.getInstance().getToolsSettings().entrySet()) {
                    if (app.getKey() != mainListItem.getId()) {
                        AppMenu appMenu = app.getValue();
                        if (!appMenu.getApplicationName().equalsIgnoreCase("")) {
                            connectedAppsList.add(appMenu.getApplicationName());
                        }
                    }
                }*/
                mimeList = getMimeList();
                for (ResolveInfo resolveInfo : installedPackageList) {
                    if (!resolveInfo.activityInfo.packageName.equalsIgnoreCase(getPackageName())) {
                        if (!checkExits(resolveInfo)) {
                            appList.add(resolveInfo);
                        }
                    }
                }
                if (showallAppBtn != null) {
                    showallAppBtn.setVisibility(View.GONE);
                }
            } else {
                appList = CoreApplication.getInstance().getApplicationByCategory(mainListItem.getId());
            }

            appListAll = new ArrayList<>();
            appListAll.addAll(installedPackageList);
            appListAll = Sorting.sortAppAssignment(AppAssignmentActivity.this, appListAll);

            if (showallAppBtn.getVisibility()!=View.VISIBLE) {
                bindList(appListAll);
            } else {
                bindList(appList);
            }
        } else {
            finish();
        }

    }

    private ArrayList<ResolveInfo> getAllapp() {
        return appListAll;
    }

    private ArrayList<ResolveInfo> getMimeList() {
        ArrayList<ResolveInfo> mimeListLocal = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            mimeListLocal.addAll(CoreApplication.getInstance().getApplicationByCategory(i));
        }
        return mimeListLocal;
    }

    private boolean checkExits(ResolveInfo resolveInfo) {
        for (ResolveInfo resolveInfo1 : mimeList) {
            if (resolveInfo != null && resolveInfo1 != null) {
                if (resolveInfo.activityInfo.packageName.equalsIgnoreCase(resolveInfo1.activityInfo
                        .packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        if (mainListItem != null) {
            toolbar.setTitle(getString(R.string.assign_an_app) + " " + mainListItem.getTitle());
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        txtErrorMessage = findViewById(R.id.txtErrorMessage);

        showallAppBtn = findViewById(R.id.txtViewAllapps);
        showallAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showallAppBtn.setVisibility(View.GONE);
                bindList(appListAll);
            }
        });

        //Added for searchbar
        cardView = findViewById(R.id.cardView);
        imgClear = findViewById(R.id.imgClear);
        edtSearch = findViewById(R.id.edtSearch);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (appAssignmentAdapter != null) {
                    appAssignmentAdapter.getFilter().filter(s.toString());
                }
                if (s.toString().length() > 0) {
                    imgClear.setVisibility(View.VISIBLE);
                } else {
                    imgClear.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setText("");
            }
        });

    }

    private void bindList(ArrayList<ResolveInfo> appList) {
        if (appList != null && appList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            txtErrorMessage.setVisibility(View.INVISIBLE);
            appList = Sorting.sortAppAssignment(this, appList);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(
                    new DividerItemDecoration(this, mLayoutManager.getOrientation()));
            if (mainListItem != null) {
                appAssignmentAdapter = new AppAssignmentAdapter(this, mainListItem.getId(),
                        appList, class_name);
                recyclerView.setAdapter(appAssignmentAdapter);
                appAssignmentAdapter.getFilter().filter(edtSearch.getText().toString().trim());
            }
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            txtErrorMessage.setVisibility(View.VISIBLE);
            if (mainListItem != null) {
                txtErrorMessage.setText("No " + mainListItem.getTitle() + " apps are installed.");
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void hideOrShowMessage(boolean isShow) {
        if (isShow) {
            recyclerView.setVisibility(View.VISIBLE);
            txtErrorMessage.setVisibility(View.INVISIBLE);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            txtErrorMessage.setVisibility(View.VISIBLE);
            txtErrorMessage.setText(R.string.no_mattched_text);
        }
    }
}

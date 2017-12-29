package co.siempo.phone.tempo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import co.siempo.phone.applist.DisableAppList;
import co.siempo.phone.applist.HeaderAppList;
import co.siempo.phone.helper.FirebaseHelper;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.event.AppInstalledEvent;
import minium.co.core.ui.CoreActivity;

/**
 * Created by hardik on 22/11/17.
 */

public class TempoAppNotificationActivity extends CoreActivity {

    long startTime = 0;
    Toolbar toolbar;
    private RecyclerView lst_appList;
    private TextView titleActionBar;
    private ImageView imgBack;
    // App list contain all the apps except social apps for display in list
    private List<DisableAppList> appList = new ArrayList<>();
    // App list contain all the messenger apps for display in list
    private List<DisableAppList> messengerList = new ArrayList<>();
    // App list contain all the messenger apps for display in list
    private List<DisableAppList> blockedAppList = new ArrayList<>();
    // App list contain all the section names for display in header list
    private List<HeaderAppList> headerList = new ArrayList<>();
    // App list contain all the social apps which are fetch from string array
    private List<String> socialAppList = new ArrayList<>();
    // App list contain all the messenger apps which are fetch from string array
    private List<String> messengerAppList = new ArrayList<>();
    private PackageManager packageManager;
    private SharedPreferences launcherPrefs;
    private ArrayList<String> disableNotificationApps = new ArrayList<>();
    private ArrayList<String> blockedApps = new ArrayList<>();
    private ArrayList<String> disableSectionList = new ArrayList<>();
    private List<String> systemAppList = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelper.getIntance().logScreenUsageTime(TempoAppNotificationActivity.class.getSimpleName(), startTime);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tempo_list_notification);
        initView();
    }

    public void initView() {

        messengerList.clear();
        messengerAppList.clear();
        appList.clear();
        socialAppList.clear();
        headerList.clear();

        // Initialize components
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle(R.string.allow_specific_apps);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lst_appList = findViewById(R.id.lst_appList);
        packageManager = getPackageManager();
        launcherPrefs = getSharedPreferences("Launcher3Prefs", 0);

        systemAppList = Arrays.asList(getResources().getStringArray(R.array.systemAppList));


        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        List<ResolveInfo> messagingResolveList = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : messagingResolveList) {
            messengerAppList.add(resolveInfo.activityInfo.packageName);
        }


        // disableNotificationApps contains of disable app list
        String disable_AppList = launcherPrefs.getString(Constants.DISABLE_APPLIST, "");
        if (!TextUtils.isEmpty(disable_AppList)) {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            disableNotificationApps = new Gson().fromJson(disable_AppList, type);
        }
        String block_AppList = launcherPrefs.getString(Constants.BLOCKED_APPLIST, "");
        if (!TextUtils.isEmpty(block_AppList)) {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            blockedApps = new Gson().fromJson(block_AppList, type);
        }

        for (ApplicationInfo applicationInfo : CoreApplication.getInstance().getPackagesList()) {

            for (String blockedApp : blockedApps) {
                if (!applicationInfo.packageName.equalsIgnoreCase(blockedApp)) {
                    disableNotificationApps.add(applicationInfo.packageName);
                }
            }
        }

        String disableList = new Gson().toJson(disableNotificationApps);
        launcherPrefs.edit().putString(Constants.DISABLE_APPLIST, disableList).commit();


        // disableSectionList contains of disable section list
        String disable_Header_AppList = launcherPrefs.getString(Constants.HEADER_APPLIST, "");
        if (!TextUtils.isEmpty(disable_Header_AppList)) {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            disableSectionList = new Gson().fromJson(disable_Header_AppList, type);
        }
        loadAndDisplayAppList();
    }

    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event.isRunning()) {
            initView();
        }
    }

    public void loadAndDisplayAppList() {
        // Load social Media Apps & Filter from app list
        for (int i = 0; i < CoreApplication.getInstance().getPackagesList().size(); i++) {
            if (blockedApps.contains(CoreApplication.getInstance().getPackagesList().get(i).packageName)) {
                DisableAppList d = new DisableAppList();
                d.applicationInfo = CoreApplication.getInstance().getPackagesList().get(i);
                d.ischecked = !blockedApps.contains(d.applicationInfo.packageName);
                blockedAppList.add(d);
            } else if (messengerAppList.contains(CoreApplication.getInstance().getPackagesList().get(i).packageName)) {
                DisableAppList d = new DisableAppList();
                d.applicationInfo = CoreApplication.getInstance().getPackagesList().get(i);
                d.ischecked = !disableNotificationApps.contains(d.applicationInfo.packageName);
                messengerList.add(d);
            } else {

                DisableAppList d = new DisableAppList();
                d.applicationInfo = CoreApplication.getInstance().getPackagesList().get(i);
                if (!TextUtils.isEmpty(d.applicationInfo.packageName) && !systemAppList.contains(d.applicationInfo.packageName)) {
                    d.ischecked = !disableNotificationApps.contains(d.applicationInfo.packageName);
                    appList.add(d);
                }
            }
        }


        if (messengerList.size() >= 0) {
            HeaderAppList d1 = new HeaderAppList();
            d1.name = "Human Direct Messaging";
            d1.ischecked = !disableSectionList.contains("Human Direct Messaging");
            headerList.add(d1);
        }


        if (appList.size() >= 0) {
            HeaderAppList d2 = new HeaderAppList();
            d2.name = "Helpful Robots";

            d2.ischecked = !disableSectionList.contains("Helpful Robots");
            headerList.add(d2);
        }

        if (blockedApps.size() >= 0) {
            HeaderAppList d3 = new HeaderAppList();
            d3.name = "All Other Apps";

            d3.ischecked = !disableSectionList.contains("All Other Apps");
            headerList.add(d3);
        }


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        lst_appList.setLayoutManager(linearLayoutManager);
        lst_appList.setHasFixedSize(true);
        TempoNotificationSectionAdapter adapter = new TempoNotificationSectionAdapter(this, appList, messengerList, blockedAppList, headerList);


        lst_appList.setAdapter(adapter);
    }
}
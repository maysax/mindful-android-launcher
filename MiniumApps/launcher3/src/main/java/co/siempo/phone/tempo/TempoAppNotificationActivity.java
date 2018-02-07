package co.siempo.phone.tempo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import co.siempo.phone.applist.AppListInfo;
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

    private List<String> pref_messengerList = new ArrayList<>();
    private ArrayList<String> pref_helpfulRobots = new ArrayList<>();
    private ArrayList<String> pref_blockedList = new ArrayList<>();

    private List<AppListInfo> messengerList = new ArrayList<>();
    private List<AppListInfo> blockedList = new ArrayList<>();
    private List<AppListInfo> helpfulRobot_List = new ArrayList<>();

    private ArrayList<String> pref_headerSectionList = new ArrayList<>();
    private List<HeaderAppList> headerSectionList = new ArrayList<>();

    private List<String> systemAppList = new ArrayList<>();

    private PackageManager packageManager;
    private SharedPreferences launcherPrefs;

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

        pref_messengerList.clear();
        pref_helpfulRobots.clear();
        pref_blockedList.clear();

        blockedList.clear();
        messengerList.clear();
        helpfulRobot_List.clear();
        headerSectionList.clear();

        // Initialize components
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle(R.string.select_apps);
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

        /**
         * Load all preference list based on share preference
         * Constants.HELPFUL_ROBOTS
         * Constants.BLOCKED_APPLIST
         */

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        List<ResolveInfo> messagingResolveList = getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : messagingResolveList) {
            pref_messengerList.add(resolveInfo.activityInfo.packageName);
        }

        String str_helpfulRobots = launcherPrefs.getString(Constants.HELPFUL_ROBOTS, "");
        if (!TextUtils.isEmpty(str_helpfulRobots)) {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            pref_helpfulRobots = new Gson().fromJson(str_helpfulRobots, type);
        }

        String str_blockedList = launcherPrefs.getString(Constants.BLOCKED_APPLIST, "");
        if (!TextUtils.isEmpty(str_blockedList)) {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            pref_blockedList = new Gson().fromJson(str_blockedList, type);
        }

        for (ApplicationInfo applicationInfo : CoreApplication.getInstance().getPackagesList()) {
            for (String blockedApp : pref_blockedList) {
                if (!applicationInfo.packageName.equalsIgnoreCase(blockedApp)) {
                    pref_helpfulRobots.add(applicationInfo.packageName);
                }
            }
        }

        String disableList = new Gson().toJson(pref_helpfulRobots);
        launcherPrefs.edit().putString(Constants.HELPFUL_ROBOTS, disableList).commit();

        String str_Header_AppList = launcherPrefs.getString(Constants.HEADER_APPLIST, "");
        if (!TextUtils.isEmpty(str_Header_AppList)) {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            pref_headerSectionList = new Gson().fromJson(str_Header_AppList, type);
        }

        loadAndDisplayAppList();
    }

    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event.isRunning()) {
            initView();
        }
    }

    /**
     * Prepare List for display based on preference list
     */
    public void loadAndDisplayAppList() {
        for (int i = 0; i < CoreApplication.getInstance().getPackagesList().size(); i++) {
            if (pref_blockedList.contains(CoreApplication.getInstance().getPackagesList().get(i).packageName)) {
                AppListInfo d = new AppListInfo();
                d.applicationInfo = CoreApplication.getInstance().getPackagesList().get(i);
                d.ischecked = !pref_blockedList.contains(d.applicationInfo.packageName);
                blockedList.add(d);
            } else if (pref_messengerList.contains(CoreApplication.getInstance().getPackagesList().get(i).packageName)) {
                AppListInfo d = new AppListInfo();
                d.applicationInfo = CoreApplication.getInstance().getPackagesList().get(i);
                d.ischecked = !pref_helpfulRobots.contains(d.applicationInfo.packageName);
                messengerList.add(d);
            } else {
                AppListInfo d = new AppListInfo();
                d.applicationInfo = CoreApplication.getInstance().getPackagesList().get(i);
                if (!TextUtils.isEmpty(d.applicationInfo.packageName) && !systemAppList.contains(d.applicationInfo.packageName)) {
                    d.ischecked = !pref_helpfulRobots.contains(d.applicationInfo.packageName);
                    helpfulRobot_List.add(d);
                }
            }
        }


        if (messengerList.size() >= 0) {
            HeaderAppList d1 = new HeaderAppList();
            d1.name = "Human direct messaging";
            d1.ischecked = !pref_headerSectionList.contains("Human direct messaging");
            headerSectionList.add(d1);
        }

        if (helpfulRobot_List.size() >= 0) {
            HeaderAppList d2 = new HeaderAppList();
            d2.name = "Helpful robots";

            d2.ischecked = !pref_headerSectionList.contains("Helpful robots");
            headerSectionList.add(d2);
        }

        if (pref_blockedList.size() >= 0) {
            HeaderAppList d3 = new HeaderAppList();
            d3.name = "All other apps";

            d3.ischecked = !pref_headerSectionList.contains("All other apps");
            headerSectionList.add(d3);
        }

        checkAppListEmpty(this, helpfulRobot_List,messengerList, blockedList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        lst_appList.setLayoutManager(linearLayoutManager);
        lst_appList.setHasFixedSize(true);
        TempoNotificationSectionAdapter adapter = new TempoNotificationSectionAdapter(this, helpfulRobot_List, messengerList, blockedList, headerSectionList);

        lst_appList.setAdapter(adapter);
    }

    public void checkAppListEmpty(Context context, List<AppListInfo> appList, List<AppListInfo> messengerList, List<AppListInfo> blockedAppList){
        if(messengerList.size()>1) {
            for (int i=0;i<messengerList.size();i++)
                if(!TextUtils.isEmpty(messengerList.get(i).errorMessage)) {
                    messengerList.remove(messengerList.get(i));
                }
        }

        if(appList.size()>1) {
            for (int i=0;i<appList.size();i++)
                if(!TextUtils.isEmpty(appList.get(i).errorMessage)) {
                    appList.remove(appList.get(i));
                }
        }

        if(blockedAppList.size()>1) {
            for (int i=0;i<blockedAppList.size();i++)
                if(!TextUtils.isEmpty(blockedAppList.get(i).errorMessage)) {
                    blockedAppList.remove(blockedAppList.get(i));
                }
        }

        if(messengerList.size() == 0){
            AppListInfo d= new AppListInfo();
            d.errorMessage=context.getResources().getString(R.string.msg_no_apps);
            messengerList.add(d);
        }

        if(appList.size() == 0){
            AppListInfo d= new AppListInfo();
            d.errorMessage=context.getResources().getString(R.string.msg_no_apps);
            appList.add(d);
        }


        if(blockedAppList.size() == 0){
            AppListInfo d= new AppListInfo();
            d.errorMessage=context.getResources().getString(R.string.msg_no_apps);
            blockedAppList.add(d);
        }

    }
}
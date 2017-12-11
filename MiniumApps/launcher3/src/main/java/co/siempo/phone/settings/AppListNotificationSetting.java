package co.siempo.phone.settings;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import co.siempo.phone.applist.DisableAppList;
import co.siempo.phone.applist.HeaderAppList;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.event.AppInstalledEvent;
import minium.co.core.ui.CoreActivity;

/**
 * Created by hardik on 22/11/17.
 */

public class AppListNotificationSetting extends CoreActivity {

    private RecyclerView lst_appList;
    private ImageView crossActionBar, settingsActionBar, btnListOrGrid;
    private TextView titleActionBar;

    // App list contain all the apps except social apps for display in list
    private List<DisableAppList> appList = new ArrayList<>();

    // App list contain all the social apps for display in list
    private List<DisableAppList> socialList = new ArrayList<>();

    // App list contain all the messenger apps for display in list
    private List<DisableAppList> messengerList = new ArrayList<>();

    // App list contain all the section names for display in header list
    private List<HeaderAppList> headerList=new ArrayList<>();

    // App list contain all the messenger apps which are fetch from string array
    private List<String> messengerAppList = new ArrayList<>();

    private PackageManager packageManager;
    private SharedPreferences launcherPrefs;
    private ArrayList<String> disableNotificationApps= new ArrayList<>();
    private ArrayList<String> disableSectionList= new ArrayList<>();
    private List<String> systemAppList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list_notification);
        initView();
    }

    public void initView(){

        messengerList.clear();
        messengerAppList.clear();
        socialList.clear();
        appList.clear();
        headerList.clear();

        // Initialize components
        lst_appList = findViewById(R.id.lst_appList);
        crossActionBar = findViewById(R.id.crossActionBar);
        settingsActionBar = findViewById(R.id.settingsActionBar);
        btnListOrGrid = findViewById(R.id.btnListOrGrid);
        titleActionBar = findViewById(R.id.titleActionBar);
        titleActionBar.setText(getResources().getString(R.string.title_managenotifications));
        packageManager= getPackageManager();
        launcherPrefs = getSharedPreferences("Launcher3Prefs", 0);

        // Add social Media List
        messengerAppList.addAll(Arrays.asList(getResources().getStringArray(R.array.messengerAppList)));

        // Hide components of header layout
        crossActionBar.setVisibility(View.GONE);
        btnListOrGrid.setVisibility(View.GONE);
        settingsActionBar.setVisibility(View.GONE);

        // disableNotificationApps contains of disable app list
        String disable_AppList=launcherPrefs.getString(Constants.DISABLE_APPLIST,"");
        if(!TextUtils.isEmpty(disable_AppList)){
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
            disableNotificationApps = new Gson().fromJson(disable_AppList, type);
        }

        // disableSectionList contains of disable section list
        String disable_Header_AppList=launcherPrefs.getString(Constants.HEADER_APPLIST,"");
        if(!TextUtils.isEmpty(disable_Header_AppList)){
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
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

    public void loadAndDisplayAppList(){
        // Load social Media Apps & Filter from app list
        for(int i = 0; i< CoreApplication.getInstance().getPackagesList().size(); i++){
                if(messengerAppList.contains(CoreApplication.getInstance().getPackagesList().get(i).packageName)){
                DisableAppList d = new DisableAppList();
                d.applicationInfo = CoreApplication.getInstance().getPackagesList().get(i);
                if(disableNotificationApps.contains(d.applicationInfo.packageName)){
                    d.ischecked=false;
                }else{
                    d.ischecked=true;
                }
                messengerList.add(d);
            }
        }



        // headerList contains all the section details information with name and enable/disable result
        if(socialList.size()>0) {
            HeaderAppList d = new HeaderAppList();
            d.name = "Social Media";
            if (disableSectionList.contains("Social Media")) {

                d.ischecked = false;
            } else {
                d.ischecked = true;
            }
            headerList.add(d);
        }


        if(messengerList.size()>0) {
            HeaderAppList d1 = new HeaderAppList();
            d1.name = "Messaging Apps";
            if (disableSectionList.contains("Messaging Apps")) {

                d1.ischecked = false;
            } else {
                d1.ischecked = true;
            }
            headerList.add(d1);
        }


        if(appList.size() > 0) {
            HeaderAppList d2 = new HeaderAppList();
            d2.name = "Other Apps";

            if (disableSectionList.contains("Other Apps")) {

                d2.ischecked = false;
            } else {
                d2.ischecked = true;
            }
            headerList.add(d2);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        lst_appList.setLayoutManager(linearLayoutManager);
        lst_appList.setHasFixedSize(true);

        NotificationSectionAdapter adapter = new NotificationSectionAdapter(this,appList,socialList,messengerList,headerList);
        lst_appList.setAdapter(adapter);
    }
}
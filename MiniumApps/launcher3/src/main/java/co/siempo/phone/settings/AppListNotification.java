package co.siempo.phone.settings;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import co.siempo.phone.applist.DisableAppList;
import co.siempo.phone.applist.HeaderAppList;
import minium.co.core.app.CoreApplication;

/**
 * Created by hardik on 22/11/17.
 */

public class AppListNotification  extends AppCompatActivity {

    private RecyclerView lst_appList;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ImageView crossActionBar, settingsActionBar, btnListOrGrid;
    private TextView titleActionBar;
    private List<DisableAppList> appList = new ArrayList<>();
    private List<DisableAppList> socialList = new ArrayList<>();
    private List<HeaderAppList> headerList=new ArrayList<>();
    private List<String> socialAppList = new ArrayList<>();
    private PackageManager packageManager;
    private SharedPreferences launcherPrefs;
    private ArrayList<String> disableNotificationApps= new ArrayList<>();
    private ArrayList<String> disableSectionList= new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list_notification);
        initView();
    }

    public void initView(){


        socialList.clear();
        appList.clear();
        socialAppList.clear();
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
        socialAppList.addAll(Arrays.asList(getResources().getStringArray(R.array.socialAppList)));

        // Hide componenets of header layout
        crossActionBar.setVisibility(View.GONE);
        btnListOrGrid.setVisibility(View.GONE);
        settingsActionBar.setVisibility(View.GONE);


        String disable_AppList=launcherPrefs.getString(CoreApplication.getInstance().DISABLE_APPLIST,"");
        if(!TextUtils.isEmpty(disable_AppList)){
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
            disableNotificationApps = new Gson().fromJson(disable_AppList, type);
        }

        String disable_Header_AppList=launcherPrefs.getString(CoreApplication.getInstance().HEADER_APPLIST,"");
        if(!TextUtils.isEmpty(disable_Header_AppList)){
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
            disableSectionList = new Gson().fromJson(disable_Header_AppList, type);
        }





        // Load social Media Apps & Filter from app list
        for(int i = 0; i< CoreApplication.getInstance().getPackagesList().size(); i++){
            if(socialAppList.contains(CoreApplication.getInstance().getPackagesList().get(i).packageName)){
                DisableAppList d = new DisableAppList();
                d.applicationInfo = CoreApplication.getInstance().getPackagesList().get(i);
                if(disableNotificationApps.contains(d.applicationInfo.packageName)){
                    d.ischecked=false;
                }else{
                    d.ischecked=true;
                }
                socialList.add(d);
            }
            else{
                DisableAppList d = new DisableAppList();
                d.applicationInfo = CoreApplication.getInstance().getPackagesList().get(i);
                if(disableNotificationApps.contains(d.applicationInfo.packageName)){
                    d.ischecked=false;
                }else{
                    d.ischecked=true;
                }
                appList.add(d);
            }
        }



        HeaderAppList d = new HeaderAppList();
        d.name ="Social List";
        if(disableSectionList.contains("Social List")){

            d.ischecked=false;
        }
        else{
            d.ischecked = true;
        }
        headerList.add(d);


        HeaderAppList d1 = new HeaderAppList();
        d1.name ="App List";

        if(disableSectionList.contains("App List")){

            d1.ischecked=false;
        }
        else{
            d1.ischecked = true;
        }
        headerList.add(d1);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        lst_appList.setLayoutManager(linearLayoutManager);
        lst_appList.setHasFixedSize(true);

        CountSectionAdapter adapter = new CountSectionAdapter(this,appList,socialList,headerList);
        lst_appList.setAdapter(adapter);

    }
}
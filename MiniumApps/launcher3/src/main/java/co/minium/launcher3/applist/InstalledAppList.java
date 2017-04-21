package co.minium.launcher3.applist;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import co.minium.launcher3.R;
import co.minium.launcher3.call.CallLogFragment_;
import co.minium.launcher3.db.ActivitiesStorage;
import co.minium.launcher3.db.ActivitiesStorageDao;
import co.minium.launcher3.db.DBUtility;
import co.minium.launcher3.event.MindfulMorgingEventStart;
import co.minium.launcher3.mm.MindfulMorningListAdapter;
import co.minium.launcher3.ui.TopFragment_;
import de.greenrobot.event.EventBus;
import minium.co.core.ui.CoreActivity;

@Fullscreen
@EActivity(R.layout.activity_installed_app_list)
public class InstalledAppList extends CoreActivity {

    @ViewById
    ListView activity_list_view;

    @ViewById
    ImageView crossActionBar;

    @Click
    void crossActionBar(){
        this.finish();
    }
    @ViewById
    TextView titleActionBar;
    @ViewById
    ImageView settingsActionBar;
    @AfterViews
    void afterViews(){

        settingsActionBar.setVisibility(View.INVISIBLE);
        titleActionBar.setText(getString(R.string.title_apps));
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        //List<ResolveInfo> pkgAppsList = getPackageManager().queryIntentActivities( mainIntent, 0);


        activity_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {



            }
        });

        /*
        PackageManager pm = getPackageManager();
       // List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        //List<ApplicationInfo> installedApps = new ArrayList<>();
        ArrayList<ApplistDataModel> arrayList = new ArrayList<>();
        ApplistDataModel applistDataModel;
        for(ApplicationInfo app : apps) {
            applistDataModel  =new ApplistDataModel();
            //checks for flags; if flagged, check if updated system app
            if((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                //installedApps.add(app);
                //it's a system app, not interested
            } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //Discard this one
                //in this case, it should be a user-installed app
                //installedApps.add(app);
                try{

                }
                String appname = getPackageManager().getApplicationLabel(app).toString();
                Drawable icon =  getPackageManager().getApplicationIcon(app.packageName);
                applistDataModel.setName(appname);
                applistDataModel.setIcon(icon);
                arrayList.add(applistDataModel);
            }
            else {
               // installedApps.add(app);
               // installedApps.add(app);

            }
        }
        */

        InstalledAppListAdapter installedAppListAdapter = new InstalledAppListAdapter(InstalledAppList.this,GetInstalledAppList());
        activity_list_view.setAdapter(installedAppListAdapter);


    }

    ArrayList<ApplistDataModel>  GetInstalledAppList()
    {
        ArrayList<ApplistDataModel> arrayList = new ArrayList<>();

        ApplistDataModel applistDataModel;
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List pkgAppsList = getPackageManager().queryIntentActivities( mainIntent, 0);
        for (Object object : pkgAppsList)
        {
            applistDataModel = new ApplistDataModel();

            ResolveInfo info = (ResolveInfo) object;
            Drawable icon    = getBaseContext().getPackageManager().getApplicationIcon(info.activityInfo.applicationInfo);
            String strAppName  	= info.activityInfo.applicationInfo.publicSourceDir.toString();
            String strPackageName  = info.activityInfo.applicationInfo.packageName.toString();
            final String title 	= (String)((info != null) ? getBaseContext().getPackageManager().getApplicationLabel(info.activityInfo.applicationInfo) : "???");

            applistDataModel.setName(title);
            applistDataModel.setIcon(icon);
            applistDataModel.setPackageName(strPackageName);

            arrayList.add(applistDataModel);
        }
        return arrayList;
    }
}

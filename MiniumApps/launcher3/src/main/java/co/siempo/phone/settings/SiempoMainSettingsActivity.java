package co.siempo.phone.settings;

import android.app.Fragment;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;

import java.util.ArrayList;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.helper.FirebaseHelper;
import co.siempo.phone.model.SettingsData;
import co.siempo.phone.notification.NotificationFragment;
import co.siempo.phone.notification.NotificationRetreat_;
import co.siempo.phone.ui.TopFragment_;
import co.siempo.phone.util.PackageUtil;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.event.AppInstalledEvent;
import minium.co.core.event.HomePressEvent;
import minium.co.core.ui.CoreActivity;


@EActivity(R.layout.activity_settings_main)
public class SiempoMainSettingsActivity extends CoreActivity {

    private ListView lst_settings;
    private ArrayList<SettingsData> arr_menuList;
    private SettingsAdapter adapter;
    private Context context;
    private final String TAG = "SiempoMainSetting";
    private long startTime=0;

    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event.isRunning()) {
            ((Launcher3App) CoreApplication.getInstance()).setAllDefaultMenusApplication();
        }
    }

    @AfterViews
    void afterViews() {
        initView();
        onClickEvents();
    }

    public void onClickEvents() {
        lst_settings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SettingsData settingsData = (SettingsData) lst_settings.getItemAtPosition(position);
                if (settingsData != null) {
                    switch (settingsData.getId()) {
                        case 1:
                            new ActivityHelper(context).openPhoneSettingsApp();
                            break;
                        case 2:
                            new ActivityHelper(context).openSiempoSettingsApp();
                            break;
                        case 3:
                            new ActivityHelper(context).openSiempoAlphaSettingsApp();
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    public void initView() {
        context = SiempoMainSettingsActivity.this;
        lst_settings = findViewById(R.id.lst_settings);


        arr_menuList = new ArrayList<>();
        SettingsData s1 = new SettingsData();
        s1.setSettingType(getString(R.string.str_phonesettings));
        s1.setId(1);
        arr_menuList.add(s1);


        SettingsData s2 = new SettingsData();
        s2.setSettingType(getString(R.string.str_siemposettings));
        s2.setId(2);
        arr_menuList.add(s2);


        if (BuildConfig.FLAVOR.equalsIgnoreCase("alpha")) {
            SettingsData s3 = new SettingsData();
            s3.setSettingType(getString(R.string.str_siempo_alphasettings));
            s3.setId(3);
            arr_menuList.add(s3);
        }

        adapter = new SettingsAdapter(this, arr_menuList);
        lst_settings.setAdapter(adapter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis();
        PackageUtil.checkPermission(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelper.getIntance().logScreenUsageTime(SiempoMainSettingsActivity.this.getClass().getSimpleName(),startTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentIndex = 0;
    }


}

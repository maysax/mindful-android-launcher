package co.siempo.phone.settings;

import android.app.Fragment;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import java.util.ArrayList;
import co.siempo.phone.R;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.model.SettingsData;
import co.siempo.phone.notification.NotificationFragment;
import co.siempo.phone.notification.NotificationRetreat_;
import co.siempo.phone.notification.StatusBarHandler;
import co.siempo.phone.ui.TopFragment_;
import minium.co.core.ui.CoreActivity;


@Fullscreen
@EActivity(R.layout.activity_settings_main)
public class SiempoMainSettingsActivity extends CoreActivity {

    private ListView lst_settings;
    private ArrayList<SettingsData> arr_menuList;
    private SettingsAdapter adapter;
    private StatusBarHandler statusBarHandler;
    private Context context;


    @AfterViews
    void afterViews() {
        initView();
        onClickEvents();
        loadTopBar();
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
        lst_settings = (ListView) findViewById(R.id.lst_settings);
        arr_menuList = new ArrayList<>();
        SettingsData s1 = new SettingsData();
        s1.setSettingType(getString(R.string.str_phonesettings));
        s1.setId(1);
        arr_menuList.add(s1);


        SettingsData s2 = new SettingsData();
        s2.setSettingType(getString(R.string.str_siemposettings));
        s2.setId(2);
        arr_menuList.add(s2);

        SettingsData s3 = new SettingsData();
        s3.setSettingType(getString(R.string.str_siempo_alphasettings));
        s3.setId(3);
        arr_menuList.add(s3);


        adapter = new SettingsAdapter(this, arr_menuList);
        lst_settings.setAdapter(adapter);
        loadTopBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusBarHandler = new StatusBarHandler(SiempoMainSettingsActivity.this);
        statusBarHandler.requestStatusBarCustomization();
    }

    @Override
    protected void onPause() {
        super.onPause();
        NotificationRetreat_.getInstance_(this.getApplicationContext()).retreat();
        try {
            if (statusBarHandler != null)
                statusBarHandler.restoreStatusBarExpansion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        if (statusBarHandler.isNotificationTrayVisible) {
            Fragment f = getFragmentManager().findFragmentById(R.id.mainView);
            if (f instanceof NotificationFragment) ;
            {
                statusBarHandler.isNotificationTrayVisible = false;

            }
        }
        super.onBackPressed();
    }

    private void loadTopBar() {
        loadFragment(TopFragment_.builder().build(), R.id.statusView, "status");
    }

}

package minium.co.settings;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ListView;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;

import java.util.ArrayList;
import java.util.List;

import minium.co.core.ui.CoreActivity;


public class SiempoMainSettingsActivity extends CoreActivity{

    private ListView lst_settings;
    private ArrayList<String> arr_menuList;
    private SettingsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);
        initView();
    }

    public void initView(){
        lst_settings=(ListView)findViewById(R.id.lst_settings);
        arr_menuList=new ArrayList<>();
        arr_menuList.add(getString(R.string.str_phonesettings));
        arr_menuList.add(getString(R.string.str_siemposettings));
        arr_menuList.add(getString(R.string.str_siempo_alphasettings));

        adapter = new SettingsAdapter(this, arr_menuList);
        lst_settings.setAdapter(adapter);
    }
}

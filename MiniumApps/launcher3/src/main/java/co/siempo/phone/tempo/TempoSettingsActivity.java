package co.siempo.phone.tempo;


import android.content.DialogInterface;
import android.content.Intent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import co.siempo.phone.MainActivity;
import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.util.PackageUtil;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.event.AppInstalledEvent;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;

@EActivity(R.layout.activity_tempo_settings)
public class TempoSettingsActivity extends CoreActivity {
    private String TAG = "TempoActivity";

    @Subscribe
    public void appInstalledEvent(AppInstalledEvent event) {
        if (event.isRunning()) {
            ((Launcher3App) CoreApplication.getInstance()).setAllDefaultMenusApplication();
        }
    }


    @AfterViews
    void afterViews() {
        loadFragment(TempoSettingsFragment_.builder().build(), R.id.tempoView, "main");

    }


    @Override
    protected void onResume() {
        super.onResume();

    }


}
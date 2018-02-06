package co.siempo.phone.activities;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import co.siempo.phone.R;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.app.Launcher3App;
import co.siempo.phone.event.AppInstalledEvent;
import co.siempo.phone.fragments.TempoSettingsFragment_;
import de.greenrobot.event.Subscribe;

@EActivity(R.layout.activity_tempo_settings)
public class SettingsActivity extends CoreActivity {

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
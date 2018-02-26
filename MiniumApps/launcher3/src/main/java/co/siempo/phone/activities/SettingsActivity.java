package co.siempo.phone.activities;

import android.content.Intent;
import android.util.Log;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import co.siempo.phone.R;
import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.event.HomePressEvent;
import co.siempo.phone.fragments.TempoSettingsFragment_;
import co.siempo.phone.log.Tracer;
import co.siempo.phone.utils.UIUtils;
import de.greenrobot.event.Subscribe;

@EActivity(R.layout.activity_tempo_settings)
public class SettingsActivity extends CoreActivity {


    @AfterViews
    void afterViews() {
        loadFragment(TempoSettingsFragment_.builder().build(), R.id.tempoView, "main");

    }


    @Override
    protected void onResume() {
        super.onResume();

    }
    @Subscribe
    public void homePressEvent(HomePressEvent event) {
        try {
            if(event.isVisible() && UIUtils.isMyLauncherDefault(this)){
               finish();
            }

        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            Tracer.e(e, e.getMessage());
        }
    }

}
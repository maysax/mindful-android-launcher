package co.siempo.phone.activities;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import co.siempo.phone.R;
import co.siempo.phone.fragments.AppMenuFragment;
import co.siempo.phone.fragments.TempoSettingsFragment_;

@EActivity(R.layout.activity_tempo_settings)
public class SettingsActivity extends CoreActivity {

    @AfterViews
    void afterViews() {
        if (getIntent().hasExtra("FlagApp")) {
            loadFragment(AppMenuFragment.newInstance(true), R.id.tempoView, "main");
        } else {
            loadFragment(TempoSettingsFragment_.builder().build(), R.id.tempoView, "main");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}
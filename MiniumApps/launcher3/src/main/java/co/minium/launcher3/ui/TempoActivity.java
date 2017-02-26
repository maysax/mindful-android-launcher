package co.minium.launcher3.ui;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;

import co.minium.launcher3.R;
import minium.co.core.ui.CoreActivity;

@Fullscreen
@EActivity(R.layout.activity_pause)
public class TempoActivity extends CoreActivity {

   /* @ViewById
    Toolbar toolbar;

    @ViewById
    HoloCircleSeekBar seekbar;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    @SystemService
    Vibrator vibrator;*/

    @AfterViews
    void afterViews() {
        //setSupportActionBar(toolbar);
        //seekbar.setOnSeekBarChangeListener(seekbarListener);
        loadFragment(TempoFragment_.builder().build(),R.id.mainView,"Main");

    }

    /*@Click
    void crossActionBar() {
        finish();
    }

    @Click
    void settingsActionBar() {
        //startActivity(new Intent(this, NotificationActivity.class));
//        UIUtils.alert(this, getString(R.string.msg_not_yet_implemented));
        *//*FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PausePreferenceFragment fragment = new PausePreferenceFragment();
        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();*//*
        loadFragment(PausePreferenceFragment_.builder().build(),R.id.mainView,"Main");
    }

    private HoloCircleSeekBar.OnCircleSeekBarChangeListener seekbarListener = new HoloCircleSeekBar.OnCircleSeekBarChangeListener() {

        @Override
        public void onProgressChanged(HoloCircleSeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(HoloCircleSeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(HoloCircleSeekBar seekBar) {

        }
    };*/

}

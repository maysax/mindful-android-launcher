package co.siempo.phone.tempo;

import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3Prefs_;
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;

@EFragment(R.layout.fragment_tempo_settings)
public class TempoSettingsFragment extends CoreFragment {

    @ViewById
    Toolbar toolbar;
    @ViewById
    TextView txtHome;
    @ViewById
    TextView txtAppMenus;
    @ViewById
    TextView txtNotification;
    @ViewById
    TextView txtAccount;
    @ViewById
    TextView txtAlphaSettings;
    @ViewById
    TextView titleActionBar;
    @Pref
    Launcher3Prefs_ launcherPrefs;

    public TempoSettingsFragment() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {
        ((CoreActivity) getActivity()).setSupportActionBar(toolbar);
        titleActionBar.setText(R.string.settings);


    }


    @Click
    void txtHome() {

    }

    @Click
    void txtAppMenus() {

    }

    @Click
    void txtNotification() {
        ((CoreActivity) getActivity()).loadChildFragment(TempoNotificationFragment_.builder().build(), R.id.tempoView);
    }

    @Click
    void txtAccount() {

    }

    @Click
    void txtAlphaSettings() {

    }

    @Click
    void imgLeft() {
        getActivity().finish();
    }


//
//    @Click
//    void imgRight() {
//        ((CoreActivity) getActivity()).loadChildFragment(TempoPreferenceFragment_.builder().build(), R.id.tempoView);
//    }


}

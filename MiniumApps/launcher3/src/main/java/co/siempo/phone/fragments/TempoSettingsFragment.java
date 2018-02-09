package co.siempo.phone.fragments;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.activities.CoreActivity;
import co.siempo.phone.app.DroidPrefs_;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.helper.ActivityHelper;

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
    @Pref
    DroidPrefs_ droidPrefs_;

    public TempoSettingsFragment() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle(R.string.settings);
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color
                .colorAccent));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        if (BuildConfig.FLAVOR.equalsIgnoreCase(context.getString(R.string.alpha))) {
            txtAlphaSettings.setVisibility(View.VISIBLE);
        } else {
            if (droidPrefs_.isAlphaSettingEnable().get()) {
                txtAlphaSettings.setVisibility(View.VISIBLE);
            } else {
                txtAlphaSettings.setVisibility(View.GONE);
            }
        }


    }


    @Click
    void txtHome() {

        ((CoreActivity) getActivity()).loadChildFragment(TempoHomeFragment_.builder()
                .build(), R.id.tempoView);
    }

    @Click
    void txtAppMenus() {
        ((CoreActivity) getActivity()).loadChildFragment(AppMenuFragment.newInstance(), R.id.tempoView);
    }

    @Click
    void txtNotification() {
        ((CoreActivity) getActivity()).loadChildFragment(TempoNotificationFragment_.builder().build(), R.id.tempoView);
    }

    @Click
    void txtAccount() {
        ((CoreActivity) getActivity()).loadChildFragment(TempoAccountSettingFragment_.builder().build(), R.id.tempoView);
    }

    @Click
    void txtAlphaSettings() {

        new ActivityHelper(context).openSiempoAlphaSettingsApp();
    }


}

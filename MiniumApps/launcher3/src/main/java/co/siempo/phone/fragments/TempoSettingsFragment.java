package co.siempo.phone.fragments;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import co.siempo.phone.BuildConfig;
import co.siempo.phone.R;
import co.siempo.phone.activities.CoreActivity;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.utils.PrefSiempo;

@EFragment(R.layout.fragment_tempo_settings)
public class TempoSettingsFragment extends CoreFragment {

    @ViewById
    Toolbar toolbar;
    @ViewById
    RelativeLayout relHome;
    @ViewById
    RelativeLayout relAppMenu;
    @ViewById
    RelativeLayout relNotification;
    @ViewById
    RelativeLayout relAccount;
    @ViewById
    RelativeLayout relAlphaSettings;
    @ViewById
    TextView titleActionBar;
    //    @Pref
//    Launcher3Prefs_ launcherPrefs;

    public TempoSettingsFragment() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle(R.string.settings);
//        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color
//                .colorAccent));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        if (BuildConfig.FLAVOR.equalsIgnoreCase(context.getString(R.string.alpha))) {
            relAlphaSettings.setVisibility(View.VISIBLE);
        } else {
            if (PrefSiempo.getInstance(context).read(PrefSiempo
                    .IS_ALPHA_SETTING_ENABLE, false)) {
                relAlphaSettings.setVisibility(View.VISIBLE);
            } else {
                relAlphaSettings.setVisibility(View.GONE);
            }
        }


    }


    @Click
    void relHome() {

        ((CoreActivity) getActivity()).loadChildFragment(TempoHomeFragment_.builder()
                .build(), R.id.tempoView);
    }

    @Click
    void relAppMenu() {
        ((CoreActivity) getActivity()).loadChildFragment(AppMenuFragment.newInstance(), R.id.tempoView);
    }

    @Click
    void relNotification() {
        ((CoreActivity) getActivity()).loadChildFragment(TempoNotificationFragment_.builder().build(), R.id.tempoView);
    }

    @Click
    void relAccount() {
        ((CoreActivity) getActivity()).loadChildFragment(AccountSettingFragment_.builder().build(), R.id.tempoView);
    }

    @Click
    void relAlphaSettings() {

        new ActivityHelper(context).openSiempoAlphaSettingsApp();
    }


}

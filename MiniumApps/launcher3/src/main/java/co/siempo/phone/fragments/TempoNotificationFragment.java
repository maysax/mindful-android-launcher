package co.siempo.phone.fragments;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.siempo.phone.R;
import co.siempo.phone.activities.TempoAppNotificationActivity;
import co.siempo.phone.app.DroidPrefs_;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.utils.PrefSiempo;

/**
 * Note : AllowPicking related stuff is now disable.
 */
@EFragment(R.layout.fragment_tempo_notifications)
public class TempoNotificationFragment extends CoreFragment {

    @ViewById
    Toolbar toolbar;

    @ViewById
    TextView titleActionBar;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    @Pref
    DroidPrefs_ droidPrefs;

    @ViewById
    TextView txtAllowAppsText;
    @ViewById
    TextView txtAllowApps;

    @ViewById
    Switch switchAllowPicking;


    @ViewById
    RelativeLayout relAllowSpecificApps;

    @ViewById
    RelativeLayout relAllowPicking;


    public TempoNotificationFragment() {
        // Required empty public constructor
    }


    @AfterViews
    void afterViews() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle(R.string.string_notification_title);
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color
                .colorAccent));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
            }
        });
        switchAllowPicking.setChecked(PrefSiempo.getInstance(context).read(PrefSiempo.ALLOW_PEAKING, true));

    }

    @Click
    void relAllowSpecificApps() {
        Intent i = new Intent(getActivity(), TempoAppNotificationActivity.class);
        startActivity(i);
    }

    @Click
    void relAllowPicking() {
        boolean allowPeaking = PrefSiempo.getInstance(context).read(PrefSiempo.ALLOW_PEAKING, true);
        allowPeaking = !allowPeaking;
        switchAllowPicking.setChecked(allowPeaking);
        PrefSiempo.getInstance(context).write(PrefSiempo.ALLOW_PEAKING, allowPeaking);
    }


}

package co.siempo.phone.fragments;

import android.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import co.siempo.phone.R;
import co.siempo.phone.activities.CoreActivity;

@EFragment(R.layout.fragment_tempo_notifications)
public class TempoNotificationAppsFragment extends CoreFragment {


    @ViewById
    Toolbar toolbar;

//    @Pref
//    Launcher3Prefs_ launcherPrefs;

    @ViewById
    TextView txtAllowAppsText;
    @ViewById
    TextView txtAllowApps;


    public TempoNotificationAppsFragment() {
        // Required empty public constructor
    }


    @AfterViews
    void afterViews() {

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle(R.string.allow_specific_apps);
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color
                .colorAccent));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
            }
        });

    }


    @Click
    void relAllowSpecificApps() {

        ((CoreActivity) getActivity()).loadChildFragment(TempoNotificationFragment_.builder().build(), R.id.tempoView);


    }


}

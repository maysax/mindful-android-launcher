package co.siempo.phone.fragments;

import android.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.siempo.phone.R;
import co.siempo.phone.activities.CoreActivity;
import co.siempo.phone.app.DroidPrefs_;
import co.siempo.phone.helper.FirebaseHelper;

@EFragment(R.layout.fragment_tempo_account_settings)
public class TempoAccountSettingFragment extends CoreFragment {


    @Pref
    DroidPrefs_ droidPrefs_;

    @ViewById
    TextView txt_privacyPolicy;

    @ViewById
    Toolbar toolbar;

    @ViewById
    TextView titleActionBar;

    @ViewById
    Switch swtch_analytics;


    @ViewById
    RelativeLayout relUpdateEmail;


    public TempoAccountSettingFragment() {
        // Required empty public constructor
    }


    @AfterViews
    void afterViews() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle(R.string.string_account_title);
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color
                .colorAccent));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
            }
        });

        txt_privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CoreActivity) getActivity()).loadChildFragment(PrivacyPolicyFragment_.builder().build(), R.id.tempoView);
            }
        });

        if (droidPrefs_.isFireBaseAnalyticsEnable().get()) {
            swtch_analytics.setChecked(true);
        } else {
            swtch_analytics.setChecked(false);
        }
        swtch_analytics.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    droidPrefs_.isFireBaseAnalyticsEnable().put(true);
                    FirebaseHelper.getIntance().getFirebaseAnalytics().setAnalyticsCollectionEnabled(true);
                } else {

                    FirebaseHelper.getIntance().getFirebaseAnalytics().setAnalyticsCollectionEnabled(false);
                    droidPrefs_.isFireBaseAnalyticsEnable().put(false);
                }
            }
        });
    }


    @Click
    void relUpdateEmail() {
        ((CoreActivity) getActivity()).loadChildFragment(TempoUpdateEmailFragment_.builder().build(), R.id.tempoView);
    }

}

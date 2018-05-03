package co.siempo.phone.fragments;

import android.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import co.siempo.phone.R;
import co.siempo.phone.utils.PrefSiempo;

@EFragment(R.layout.fragment_tempo_home)
public class TempoHomeFragment extends CoreFragment {

    @ViewById
    Toolbar toolbar;

    @ViewById
    Switch switchDisableIntentionsControls;

    @ViewById
    RelativeLayout relAllowSpecificApps;

    @ViewById
    Switch switchCustomBackground;

    @ViewById
    RelativeLayout relCustomBackground;

    @ViewById
    Switch switchDarkTheme;

    @ViewById
    RelativeLayout relDarkTheme;

    public TempoHomeFragment() {
        // Required empty public constructor
    }


    @AfterViews
    void afterViews() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_blue_24dp);
        toolbar.setTitle(R.string.homescreen);
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color
                .colorAccent));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
            }
        });
        switchDisableIntentionsControls.setChecked(PrefSiempo.getInstance(context).read(PrefSiempo.IS_INTENTION_ENABLE, false));
        switchDisableIntentionsControls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PrefSiempo.getInstance(context).write(PrefSiempo
                        .IS_INTENTION_ENABLE, isChecked);
            }
        });
    }

    @Click
    void relAllowSpecificApps() {

        switchDisableIntentionsControls.performClick();
    }

    @Click
    void relCustomBackground() {
        switchCustomBackground.performClick();
    }

    @Click
    void relDarkTheme() {
        switchDarkTheme.performClick();
    }


}

package co.siempo.phone.tempo;

import android.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.siempo.phone.R;
import minium.co.core.app.DroidPrefs_;
import minium.co.core.ui.CoreFragment;

@EFragment(R.layout.fragment_tempo_home)
public class TempoHomeFragment extends CoreFragment {

    @ViewById
    Toolbar toolbar;

    @Pref
    DroidPrefs_ droidPrefs;

    @ViewById
    Switch switchDisableIntentionsControls;

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
        switchDisableIntentionsControls.setChecked(droidPrefs.isIntentionEnable().get());
        switchDisableIntentionsControls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                droidPrefs.isIntentionEnable().put(isChecked);
            }
        });
    }


}

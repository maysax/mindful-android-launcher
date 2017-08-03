package co.siempo.phone.mm;

import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3Prefs_;
import minium.co.core.ui.CoreFragment;

/**
 * Created by tkb on 2017-03-13.
 */

@EFragment(R.layout.away_fragment)
public class AwayFragment extends CoreFragment {

    @Pref
    Launcher3Prefs_ launcherPrefs;

    public AwayFragment() {
        // Required empty public constructor
    }

    @ViewById
    Switch switch_away;

    @ViewById
    ImageView crossActionBar;

    @ViewById
    EditText text_update_message;

    @Click
    void crossActionBar() {
        launcherPrefs.awayMessage().put(text_update_message.getText().toString());
        getActivity().onBackPressed();

    }

    @AfterViews
    void afterViews() {
        switch_away.setOnCheckedChangeListener(checkedChangeListener);
        switch_away.setChecked(launcherPrefs.isAwayChecked().get());
        text_update_message.setText(launcherPrefs.awayMessage().get());
    }

    private Switch.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            launcherPrefs.isAwayChecked().put(isChecked);

        }
    };

}

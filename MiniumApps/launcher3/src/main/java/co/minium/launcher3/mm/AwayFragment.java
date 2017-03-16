package co.minium.launcher3.mm;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.minium.launcher3.R;
import co.minium.launcher3.app.Launcher3Prefs_;
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;

/**
 * Created by tkb on 2017-03-13.
 */

@EFragment(R.layout.away_fragment)
public class AwayFragment extends CoreFragment {

    @Pref
    Launcher3Prefs_ launcherPrefs;

    /*@ViewById
    Switch  switch_away;*/

    /*@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.away_fragment, parent, false);

    }*/
    public AwayFragment() {
        // Required empty public constructor
    }
    @ViewById
    Switch switch_away;

    @ViewById
    ImageView crossActionBar;

    @Click
    void crossActionBar(){
        getActivity().onBackPressed();
    }

    @AfterViews
    void afterViews() {
        //Switch switch_away = (Switch)view.findViewById(R.id.switch_away);
        switch_away.setOnCheckedChangeListener(checkedChangeListener);
        switch_away.setChecked(launcherPrefs.isAwayChecked().get());

       /* ImageView crossActionBar = (ImageView) view.findViewById(R.id.crossActionBar);
        crossActionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().onBackPressed();
            }
        });*/

        /*switch_away.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                launcherPrefs.isAwayChecked().put(isChecked);
            }
        });*/

    }

    private Switch.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            launcherPrefs.isAwayChecked().put(isChecked);

        }
    };
    /*
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Switch switch_away = (Switch)view.findViewById(R.id.switch_away);
        switch_away.setChecked(launcherPrefs.isAwayChecked().get());

        ImageView crossActionBar = (ImageView) view.findViewById(R.id.crossActionBar);
        crossActionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().onBackPressed();
            }
        });

        switch_away.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //switch_away.setChecked(isChecked);

                launcherPrefs.isAwayChecked().put(isChecked);
            }
        });
    }
*/

    /*@Click
    void switch_away(){

    }*/
}

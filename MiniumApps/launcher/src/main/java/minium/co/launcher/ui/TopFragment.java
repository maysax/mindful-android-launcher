package minium.co.launcher.ui;


import android.support.v4.app.Fragment;

import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.androidannotations.annotations.EFragment;

import minium.co.core.ui.CoreFragment;
import minium.co.launcher.R;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_top)
public class TopFragment extends CoreFragment {

    FontAwesomeIcons [] batteryIcons = {
            FontAwesomeIcons.fa_battery_0,
            FontAwesomeIcons.fa_battery_1,
            FontAwesomeIcons.fa_battery_2,
            FontAwesomeIcons.fa_battery_3,
            FontAwesomeIcons.fa_battery_4
    };


    public TopFragment() {
        // Required empty public constructor
    }

}

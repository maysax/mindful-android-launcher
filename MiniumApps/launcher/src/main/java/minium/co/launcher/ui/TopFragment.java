package minium.co.launcher.ui;


import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.ViewById;

import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;
import minium.co.launcher.R;
import minium.co.launcher.battery.BatteryChangeEvent;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_top)
public class TopFragment extends CoreFragment {

    @ViewById
    TextView iTxt1;

    @ViewById
    TextView iTxt2;

    @ViewById
    TextView iTxt3;

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

    @AfterViews
    void afterViews() {
        iTxt3.setText(getString(R.string.format_battery, "90%"));
        //iTxt3.setCompoundDrawablesWithIntrinsicBounds(new IconDrawable(getContext(), batteryIcons [3]), null, null, null);
    }

    @Trace(tag = TRACE_TAG)
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onBatteryLevelChange(BatteryChangeEvent event) {
        UIUtils.toast(getContext(), "Battery level: " + event.getBatteryPct());
    }
}

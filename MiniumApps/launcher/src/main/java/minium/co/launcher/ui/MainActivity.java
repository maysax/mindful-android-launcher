package minium.co.launcher.ui;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.Trace;

import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import minium.co.core.config.Config;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.UIUtils;
import minium.co.launcher.R;
import minium.co.launcher.battery.BatteryChangeEvent;

@Fullscreen
@EActivity(R.layout.activity_main)
public class MainActivity extends CoreActivity {

    private final String TRACE_TAG = Config.TRACE_TAG + "MainActivity";

    @Trace(tag = TRACE_TAG)
    @AfterViews
    void afterViews() {
        loadTopView();
        loadMainView();
        loadBottomView();
    }

    @Trace(tag = TRACE_TAG)
    void loadTopView() {
        loadFragment(TopFragment_.builder().build(), R.id.topView);
    }

    @Trace(tag = TRACE_TAG)
    void loadBottomView() {
        loadFragment(BottomFragment_.builder().build(), R.id.bottomView);
    }

    @Trace(tag = TRACE_TAG)
    void loadMainView() {
        loadFragment(MainFragment_.builder().build());
    }

    @Trace(tag = TRACE_TAG)
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onBatteryLevelChange(BatteryChangeEvent event) {
        UIUtils.toast(this, "Battery level: " + event.getBatteryPct());
    }
}

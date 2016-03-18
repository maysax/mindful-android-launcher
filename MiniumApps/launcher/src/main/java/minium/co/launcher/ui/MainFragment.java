package minium.co.launcher.ui;

import android.widget.TextView;

import com.joanzapata.iconify.widget.IconButton;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Trace;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.ViewsById;
import org.androidannotations.annotations.res.StringArrayRes;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import minium.co.core.config.Config;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;
import minium.co.launcher.R;
import minium.co.launcher.clock.ClockTicker;
import minium.co.launcher.clock.ClockTickerEvent;
import minium.co.launcher.helper.ActivityHelper;


/**

 */
@EFragment(R.layout.fragment_main)
public class MainFragment extends CoreFragment {

    protected final String TRACE_TAG = Config.TRACE_TAG + "MainFragment";

    @ViewById
    TextView txtTime;

    @ViewById
    TextView txtDate;

    @ViewsById({R.id.ib00, R.id.ib01, R.id.ib02, R.id.ib10, R.id.ib11, R.id.ib12, R.id.ib20, R.id.ib21, R.id.ib22})
    List<IconButton> ibItems;

    @StringArrayRes
    String[] appNamesWithIcon;

    SimpleDateFormat sdfTime;
    SimpleDateFormat sdfDate;

    public MainFragment() {
        // Required empty public constructor
    }

    @Trace(tag = TRACE_TAG)
    @AfterViews
    void afterViews() {
        sdfTime = new SimpleDateFormat(Config.TIME_FORMAT, Locale.US);
        sdfDate = new SimpleDateFormat(Config.DATE_FORMAT, Locale.US);
        new ClockTicker().start();
        setupViews();
    }

    @Trace(tag = TRACE_TAG)
    void setupViews() {
        int index = 0;
        for (IconButton btn : ibItems) {
            btn.setText(appNamesWithIcon[index]);
            index++;
        }
    }


    //@Trace(tag = TRACE_TAG)
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onClockTick(ClockTickerEvent event) {
        txtTime.setText(sdfTime.format(event.getCalendar().getTime()));
        txtDate.setText(sdfDate.format(event.getCalendar().getTime()));
        txtTime.invalidate();
        txtDate.invalidate();
    }

    @Click
    void ib00() {
        // opening contacts app
        if (!new ActivityHelper(getContext()).openContactsApp())
            UIUtils.alert(getContext(), getString(R.string.msg_not_yet_implemented));
    }

    @Click
    void ib01() {
        // opening messages app
        if (!new ActivityHelper(getContext()).openMessagingApp())
            UIUtils.alert(getContext(), getString(R.string.msg_not_yet_implemented));
    }

    @Click
    void ib02() {
        // What is Focus app? I do not have any idea.
        UIUtils.alert(getContext(), getString(R.string.msg_not_yet_implemented));
    }

    @Click
    void ib10() {
        // opening clock app
        UIUtils.alert(getContext(), getString(R.string.msg_not_yet_implemented));
    }

    @Click
    void ib11() {
        // opening dialer app
        if (!new ActivityHelper(getContext()).openDialerApp())
            UIUtils.alert(getContext(), getString(R.string.msg_not_yet_implemented));
    }

    @Click
    void ib12() {
        // opening calculator app
        if (!new ActivityHelper(getContext()).openCalculatorApp())
            UIUtils.alert(getContext(), getString(R.string.msg_not_yet_implemented));
    }

    @Click
    void ib20() {
        UIUtils.alert(getContext(), getString(R.string.msg_not_yet_implemented));
    }

    @Click
    void ib21() {
        UIUtils.alert(getContext(), getString(R.string.msg_not_yet_implemented));
    }

    @Click
    void ib22() {
        // opening settings app
        if (!new ActivityHelper(getContext()).openSettingsApp())
            UIUtils.alert(getContext(), getString(R.string.msg_not_yet_implemented));
    }
}

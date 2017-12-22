package co.siempo.phone.tempo;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.settings.AppListNotification;
import co.siempo.phone.ui.SiempoPermissionActivity;
import co.siempo.phone.util.PermissionUtil;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;

@EFragment(R.layout.fragment_tempo_notifications)
public class TempoNotificationFragment extends CoreFragment {


    @ViewById
    Toolbar toolbar;

    @ViewById
    TextView titleActionBar;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    @ViewById
    Switch switchDisableNotificationControls;
    @ViewById
    Switch switchAllowOnLockScreen;
    @ViewById
    Switch switchAllowPeaking;
    @ViewById
    TextView txtAllowPeakingText;
    @ViewById
    TextView txtAllowOnLockScreenText;
    @ViewById
    TextView txtAllowAppsText;
    @ViewById
    TextView txtAllowApps;
    @ViewById
    TextView txtAllowPeaking;
    @ViewById
    TextView txtAllowOnLockScreen;
    @ViewById
    TextView txtDisableNotificationControls;

    @ViewById
    RelativeLayout relAllowSpecificApps;


    public TempoNotificationFragment() {
        // Required empty public constructor
    }

    @Click
    void imgLeft() {
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
        //((CoreActivity)getActivity()).finish();
    }


    @AfterViews
    void afterViews() {
        ((CoreActivity) getActivity()).setSupportActionBar(toolbar);
        titleActionBar.setText(R.string.string_notification_title);
    }


    @CheckedChange
    void switchDisableNotificationControls(CompoundButton btn, boolean isChecked) {
        if (isChecked) {
            txtAllowOnLockScreen.setVisibility(View.GONE);
            txtAllowPeaking.setVisibility(View.GONE);
            txtAllowApps.setVisibility(View.GONE);
            txtAllowAppsText.setVisibility(View.GONE);
            txtAllowOnLockScreenText.setVisibility(View.GONE);
            switchAllowPeaking.setVisibility(View.GONE);
            switchAllowOnLockScreen.setVisibility(View.GONE);
            txtAllowPeakingText.setVisibility(View.GONE);
        } else {
            txtAllowOnLockScreen.setVisibility(View.VISIBLE);
            txtAllowPeaking.setVisibility(View.VISIBLE);
            txtAllowApps.setVisibility(View.VISIBLE);
            txtAllowAppsText.setVisibility(View.VISIBLE);
            txtAllowOnLockScreenText.setVisibility(View.VISIBLE);
            switchAllowPeaking.setVisibility(View.VISIBLE);
            switchAllowOnLockScreen.setVisibility(View.VISIBLE);
            txtAllowPeakingText.setVisibility(View.VISIBLE);
        }

    }

    @CheckedChange
    void switchAllowOnLockScreen(CompoundButton btn, boolean isChecked) {
        if (isChecked) {
            txtAllowOnLockScreenText.setText("On. All notifications will be hidden from the lock screen.");
        } else {
            txtAllowOnLockScreenText.setText("Off. All notifications will be hidden from the lock screen.");
        }
    }

    @CheckedChange
    void switchAllowPeaking(CompoundButton btn, boolean isChecked) {
        if (isChecked) {
            txtAllowPeakingText.setText("On. The status bar will show you when you have new notifications in your tray, but your tray won't pop up automatically.");

        } else {
            txtAllowPeakingText.setText("Off. The status bar will show you when you have new notifications in your tray, but your tray won't pop up automatically.");
        }
    }

    @Click
    void relAllowSpecificApps() {

        try {
            Intent i = new Intent(getActivity(), TempoAppNotificationActivity.class);
            getActivity().startActivity(i);
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
            CoreApplication.getInstance().logException(e);
        }
    }


}

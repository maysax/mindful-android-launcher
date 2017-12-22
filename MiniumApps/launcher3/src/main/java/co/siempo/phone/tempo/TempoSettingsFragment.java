package co.siempo.phone.tempo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import co.siempo.phone.R;
import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.db.CallStorageDao;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.event.TempoEvent;
import co.siempo.phone.util.AudioUtils;
import de.greenrobot.event.EventBus;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.DateUtils;

import static co.siempo.phone.app.Constants.DEFAULT_TEMPO_MINUTE;
import static co.siempo.phone.app.Launcher3App.DND_START_STOP_ACTION;

@EFragment(R.layout.fragment_tempo_settings)
public class TempoSettingsFragment extends CoreFragment {

    public TempoSettingsFragment() {
        // Required empty public constructor
    }

    @ViewById
    Toolbar toolbar;

    @ViewById
    TextView txtHome;

    @ViewById
    TextView txtAppMenus;

    @ViewById
    TextView txtNotification;

    @ViewById
    TextView txtAccount;

    @ViewById
    TextView txtAlphaSettings;

    @ViewById
    TextView titleActionBar;

    @Pref
    Launcher3Prefs_ launcherPrefs;


    @AfterViews
    void afterViews() {
        ((CoreActivity) getActivity()).setSupportActionBar(toolbar);
        titleActionBar.setText(R.string.settings);


    }


    @Click
    void txtHome() {

    }

    @Click
    void txtAppMenus() {

    }
    @Click
    void txtNotification() {
        ((CoreActivity) getActivity()).loadChildFragment(TempoNotificationFragment_.builder().build(), R.id.tempoView);
    }
    @Click
    void txtAccount() {

    }
    @Click
    void txtAlphaSettings() {

    }
    @Click
    void imgLeft() {
        getActivity().finish();
    }


//
//    @Click
//    void imgRight() {
//        ((CoreActivity) getActivity()).loadChildFragment(TempoPreferenceFragment_.builder().build(), R.id.tempoView);
//    }


}

package co.minium.launcher3.mm;

import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import co.minium.launcher3.R;
import co.minium.launcher3.app.Launcher3Prefs_;
import co.minium.launcher3.db.DBUtility;
import co.minium.launcher3.mm.model.ActivitiesStorage;
import co.minium.launcher3.mm.model.ActivitiesStorageDao;
import co.minium.launcher3.pause.PausePreferenceFragment_;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;

/**
 * Created by Shahab on 3/16/2017.
 */
@EFragment(R.layout.fragment_minfulmorning_activated)
public class MinfulMorningActivated extends CoreFragment {

    @ViewById
    Toolbar toolbar;

    @ViewById
    HoloCircleSeekBar seekbar;

    @ViewById
    ImageView imgBackground;

    @ViewById
    TextView txtRemainingTime;

    //@ViewById
    //TextView txtEndingTime;

    @ViewById
    TextView titleActionBar;

    @Pref
    Launcher3Prefs_ launcherPrefs;

    @ViewById
    ImageButton pause_button;

    @FragmentArg
    int startPosition = 0;

    Handler handler;

    private int atMillis = 0;

    int maxMillis = 0;
    List<ActivitiesStorage> activitiesStorageList;
    public MinfulMorningActivated() {
        // Required empty public constructor
    }

    @AfterViews
    void afterViews() {
        ((CoreActivity)getActivity()).setSupportActionBar(toolbar);
        pause_button.setVisibility(View.INVISIBLE);
        handler = new Handler();
        titleActionBar.setText(R.string.title_mindfulMorning);
        activitiesStorageList =  DBUtility.getActivitySession()
                .queryBuilder().where(ActivitiesStorageDao.Properties.Time.notEq(0)).list();

        maxMillis = activitiesStorageList.get(startPosition).getTime() * 60 * 1000;
        startPause();
    }

    private void startPause() {
        if (maxMillis < 1) return;
        seekbar.setMax(maxMillis / (60 * 1000));
        seekbar.setValue(0);
        imgBackground.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
        launcherPrefs.isPauseActive().put(true);
        handler.postDelayed(pauseActiveRunnable, 1000);
        Calendar calStart = Calendar.getInstance(Locale.US);
        Calendar calEnd = Calendar.getInstance(Locale.US);
        calEnd.add(Calendar.MILLISECOND, maxMillis);
        txtRemainingTime.setText(new SimpleDateFormat("hh:mm a", Locale.US).format(calStart.getTime())+"-"+new SimpleDateFormat("hh:mm a", Locale.US).format(calEnd.getTime()));
    }

    @Click
    void imgLeft() {
        getActivity().onBackPressed();
    }

    @Click
    void imgRight() {
        ((CoreActivity)getActivity()).loadChildFragment(PausePreferenceFragment_.builder().build(),R.id.mainView);
    }

    private Runnable pauseActiveRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                atMillis += 1000;

                if (atMillis >= maxMillis) {
                    stopPause();
                } else {
                    Tracer.d("Now : " + atMillis + " seekbar value: " + atMillis / (1000 * 60.0f));
                    seekbar.setValue(atMillis / (1000 * 60.0f));
                  // txtRemainingTime.setText(String.format(Locale.US, "%d minute", TimeUnit.MILLISECONDS.toMinutes(maxMillis - atMillis)));
                    handler.postDelayed(this, 1000);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Click
    void pause_button(){
        pause_button.setVisibility(View.INVISIBLE);
        atMillis = 0;
        startPosition=0;
        maxMillis = activitiesStorageList.get(startPosition).getTime() * 60 * 1000;
        startPause();

    }
    private void stopPause() {
        activitiesStorageList.remove(startPosition);

        if (activitiesStorageList.size()==0){
            seekbar.setValue(0);
            seekbar.setShowTitle(false);
            launcherPrefs.isPauseActive().put(false);
            getActivity().finish();
        }else {
            //if (pause_button.visible)
            pause_button.setVisibility(View.VISIBLE);
        }
        handler.removeCallbacks(pauseActiveRunnable);

    }
}

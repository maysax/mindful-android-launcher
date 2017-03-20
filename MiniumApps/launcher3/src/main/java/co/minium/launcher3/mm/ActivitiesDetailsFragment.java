package co.minium.launcher3.mm;

import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import co.minium.launcher3.R;
import co.minium.launcher3.mm.model.ActivitiesStorage;
import co.minium.launcher3.mm.model.ActivitiesStorageDao;
import co.minium.launcher3.mm.model.DBUtility;
import minium.co.core.ui.CoreFragment;

/**
 * Created by tkb on 2017-03-10.
 */
@EFragment(R.layout.meditation_time)
public class ActivitiesDetailsFragment extends CoreFragment {
    @FragmentArg
    String title;
    ActivitiesStorage activitiesStorage;

    @ViewById
    TextView titleActionBar;
    @ViewById
    ImageView crossActionBar;
    @ViewById
    Button pause_button;
    @ViewById
    HoloCircleSeekBar seekbar;

    @Click
    void pause_button() {
        MindfulMorningActivity_.intent(getActivity()).start();
    }

    @Click
    void crossActionBar() {
        activitiesStorage.setTime(seekbar.getValue());
        DBUtility.GetActivitySession().update(activitiesStorage);
        getActivity().onBackPressed();
    }

    @AfterViews
    void afterViews() {
        titleActionBar.setText(title+" Timer");
        activitiesStorage = DBUtility.GetActivitySession()
                .queryBuilder().where(ActivitiesStorageDao.Properties.Name.eq(title)).unique();


        seekbar.setOnSeekBarChangeListener(new HoloCircleSeekBar.OnCircleSeekBarChangeListener() {
            @Override
            public void onProgressChanged(HoloCircleSeekBar seekBar, int progress, boolean fromUser) {
                Log.e("TKB", " onProgressChanged");
            }

            @Override
            public void onStartTrackingTouch(HoloCircleSeekBar seekBar) {
                Log.e("TKB", " onStartTrackingTouch");

            }

            @Override
            public void onStopTrackingTouch(HoloCircleSeekBar seekBar) {
                Log.e("TKB", " onStopTrackingTouch");

            }
        });

        seekbar.setValue(activitiesStorage.getTime());
    }


    /*private void startPause() {
        if (maxMillis < 1) return;
        seekbar.setMax(maxMillis / (60 * 1000));
        seekbar.setValue(0);
        imgBackground.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in));
        launcherPrefs.isPauseActive().put(true);
        handler.postDelayed(pauseActiveRunnable, 1000);
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.add(Calendar.MILLISECOND, maxMillis);
        txtEndingTime.setText(new SimpleDateFormat("hh:mm a", Locale.US).format(cal.getTime()));
    }
*/
}

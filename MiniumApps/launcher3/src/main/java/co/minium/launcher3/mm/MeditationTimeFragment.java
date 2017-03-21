package co.minium.launcher3.mm;

import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import co.minium.launcher3.R;
import co.minium.launcher3.mm.model.ActivitiesStorage;
import co.minium.launcher3.mm.model.ActivitiesStorageDao;
import co.minium.launcher3.db.DBUtility;
import minium.co.core.ui.CoreFragment;

/**
 * Created by tkb on 2017-03-10.
 */
@EFragment(R.layout.meditation_time)
public class MeditationTimeFragment extends CoreFragment {
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
        DBUtility.getActivitySession().update(activitiesStorage);
        getActivity().onBackPressed();
    }

    @AfterViews
    void afterViews() {
        activitiesStorage = DBUtility.getActivitySession()
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

}

package co.minium.launcher3.mm;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jesusm.holocircleseekbar.lib.HoloCircleSeekBar;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import co.minium.launcher3.R;
import co.minium.launcher3.mm.model.ActivitiesStorage;
import co.minium.launcher3.mm.model.ActivitiesStorageDao;
import co.minium.launcher3.mm.model.DBUtility;
import minium.co.core.ui.CoreActivity;

/**
 * Created by tkb on 2017-03-10.
 */
@EFragment
public class MeditationTimeFragment extends Fragment {
    @FragmentArg
    String title;
    HoloCircleSeekBar seekbar;
    ActivitiesStorage activitiesStorage;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.meditation_time, parent, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        activitiesStorage = DBUtility.GetActivitySession()
                .queryBuilder().where(ActivitiesStorageDao.Properties.Name.eq(title)).unique();
        TextView titleActionBar = (TextView)view.findViewById(R.id.titleActionBar);
        titleActionBar.setText(activitiesStorage.getName());

        ImageView crossActionBar = (ImageView) view.findViewById(R.id.crossActionBar);
        crossActionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activitiesStorage.setTime(seekbar.getValue());
                DBUtility.GetActivitySession().update(activitiesStorage);
                getActivity().onBackPressed();
            }
        });

        Button pause_button = (Button)view.findViewById(R.id.pause_button);
        pause_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CoreActivity)getActivity()).loadChildFragment(new MindfulMorningFragment_(),R.id.mainView);
            }
        });

        seekbar = (HoloCircleSeekBar)view.findViewById(R.id.seekbar);
        seekbar.setOnSeekBarChangeListener(new HoloCircleSeekBar.OnCircleSeekBarChangeListener() {
            @Override
            public void onProgressChanged(HoloCircleSeekBar seekBar, int progress, boolean fromUser) {
                Log.e("TKB"," onProgressChanged");
            }

            @Override
            public void onStartTrackingTouch(HoloCircleSeekBar seekBar) {
                Log.e("TKB"," onStartTrackingTouch");

            }

            @Override
            public void onStopTrackingTouch(HoloCircleSeekBar seekBar) {
                Log.e("TKB"," onStopTrackingTouch");

            }
        });


       // seekbar.setInitPosition(activitiesStorage.getTime());
        seekbar.setValue(activitiesStorage.getTime());
    }
}

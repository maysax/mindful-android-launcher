package co.minium.launcher3.mm;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;

import java.util.Calendar;
import java.util.Locale;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelScrollListener;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;
import co.minium.launcher3.R;
import minium.co.core.ui.CoreActivity;

public class TimePickerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_time_picker, parent, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        TableRow row1 = (TableRow) view.findViewById(R.id.row1);
        TableRow row2 = (TableRow) view.findViewById(R.id.row2);
        TableRow row3 = (TableRow) view.findViewById(R.id.row3);
        TableRow row4 = (TableRow) view.findViewById(R.id.row4);
        ImageView crossActionBar = (ImageView) view.findViewById(R.id.crossActionBar);
        crossActionBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        row1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CoreActivity)getActivity()).loadChildFragment(new AwayFragment_(),R.id.mainView);
            }
        });
        row2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CoreActivity)getActivity()).loadChildFragment(new ActivitiesFragment(),R.id.mainView);
            }
        });
        row3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        row4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CoreActivity)getActivity()).loadChildFragment(new RepeatFragment(),R.id.mainView);
            }
        });
        final AbstractWheel hours = (AbstractWheel) view.findViewById(R.id.hour_horizontal);
        NumericWheelAdapter hourAdapter = new NumericWheelAdapter(getActivity(), 1, 12, "%01d");
        hourAdapter.setItemResource(R.layout.wheel_text_centered);
        hourAdapter.setItemTextResource(R.id.text);
        hours.setViewAdapter(hourAdapter);

        hours.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(AbstractWheel wheel) {
                Log.e("TKB","onScrollingStarted");
            }

            @Override
            public void onScrollingFinished(AbstractWheel wheel) {
                Log.e("TKB","onScrollingFinished");

            }
        });

        final AbstractWheel mins = (AbstractWheel) view.findViewById(R.id.mins);
        NumericWheelAdapter minAdapter = new NumericWheelAdapter(getActivity(), 0, 59, "%02d");
        minAdapter.setItemResource(R.layout.wheel_text_centered_dark_back);
        minAdapter.setItemTextResource(R.id.text);
        mins.setViewAdapter(minAdapter);
        mins.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(AbstractWheel wheel) {

            }

            @Override
            public void onScrollingFinished(AbstractWheel wheel) {

            }
        });

        final AbstractWheel ampm = (AbstractWheel) view.findViewById(R.id.ampm);
        ArrayWheelAdapter<String> ampmAdapter =
                new ArrayWheelAdapter<>(getActivity(), new String[] {"AM", "PM"});
        ampmAdapter.setItemResource(R.layout.wheel_text_centered_am_pm);
        ampmAdapter.setItemTextResource(R.id.text);
        ampm.setViewAdapter(ampmAdapter);
        ampm.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(AbstractWheel wheel) {

            }

            @Override
            public void onScrollingFinished(AbstractWheel wheel) {

            }
        });
        // set current time
        Calendar calendar = Calendar.getInstance(Locale.US);
        hours.setCurrentItem(calendar.get(Calendar.HOUR_OF_DAY));
        mins.setCurrentItem(calendar.get(Calendar.MINUTE));
        ampm.setCurrentItem(calendar.get(Calendar.AM_PM));
    }


}
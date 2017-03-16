package co.minium.launcher3.mm;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.Locale;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelScrollListener;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;
import co.minium.launcher3.R;
import co.minium.launcher3.ui.TempoActivity;
import minium.co.core.ui.CoreActivity;

@EFragment(R.layout.fragment_time_picker)
public class TimePickerFragment extends Fragment {


    @ViewById
    TableRow row1,row2,row3,row4;

    @ViewById
    ImageView crossActionBar;

    @ViewById
    TextView time;

    @Click
    void row1(){
        ((CoreActivity)getActivity()).loadChildFragment(new AwayFragment_(),R.id.mainView);
    }
    @Click
    void row2(){
        ((CoreActivity)getActivity()).loadChildFragment(new ActivitiesFragment(),R.id.mainView);
    }
    @Click
    void row3(){

    }
    @Click
    void row4(){
        ((CoreActivity)getActivity()).loadChildFragment(new RepeatFragment(),R.id.mainView);

    }
    @Click
    void crossActionBar(){
        getActivity().onBackPressed();
    }

    @ViewById
    AbstractWheel hour_horizontal,mins,ampm;
    @AfterViews
    void afterViews(){

        //String _hour,_minute,_amPM;

        NumericWheelAdapter hourAdapter = new NumericWheelAdapter(getActivity(), 1, 12, "%01d");
        hourAdapter.setItemResource(R.layout.wheel_text_centered);
        hourAdapter.setItemTextResource(R.id.text);
        hour_horizontal.setViewAdapter(hourAdapter);

        hour_horizontal.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(AbstractWheel wheel) {
                Log.e("TKB","onScrollingStarted");

                time.setText((Integer.parseInt(wheel.getCurrentItem()+"")+1)+":"+mins.getCurrentItem()+" "+ampm.getCurrentItem());
            }

            @Override
            public void onScrollingFinished(AbstractWheel wheel) {
                Log.e("TKB","onScrollingFinished");
                time.setText((Integer.parseInt(wheel.getCurrentItem()+"")+1)+":"+mins.getCurrentItem()+" "+ampm.getCurrentItem());
            }
        });

        NumericWheelAdapter minAdapter = new NumericWheelAdapter(getActivity(), 0, 59, "%02d");
        minAdapter.setItemResource(R.layout.wheel_text_centered_dark_back);
        minAdapter.setItemTextResource(R.id.text);
        mins.setViewAdapter(minAdapter);
        mins.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(AbstractWheel wheel) {
                time.setText(hour_horizontal.getCurrentItem()+":"+wheel.getCurrentItem()+" "+ampm.getCurrentItem()+"");

            }

            @Override
            public void onScrollingFinished(AbstractWheel wheel) {
                time.setText(hour_horizontal.getCurrentItem()+":"+wheel.getCurrentItem()+" "+ampm.getCurrentItem()+"");

            }
        });

        ArrayWheelAdapter<String> ampmAdapter =
                new ArrayWheelAdapter<>(getActivity(), new String[] {"AM", "PM"});
        ampmAdapter.setItemResource(R.layout.wheel_text_centered_am_pm);
        ampmAdapter.setItemTextResource(R.id.text);
        ampm.setViewAdapter(ampmAdapter);
        ampm.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(AbstractWheel wheel) {
                time.setText(hour_horizontal.getCurrentItem()+":"+mins.getCurrentItem()+" "+wheel.getCurrentItem()+"");

            }

            @Override
            public void onScrollingFinished(AbstractWheel wheel) {
                time.setText(hour_horizontal.getCurrentItem()+":"+mins.getCurrentItem()+" "+wheel.getCurrentItem()+"");

            }
        });
        // set current time
        Calendar calendar = Calendar.getInstance(Locale.US);
        hour_horizontal.setCurrentItem(calendar.get(Calendar.HOUR_OF_DAY));
        mins.setCurrentItem(calendar.get(Calendar.MINUTE));
        ampm.setCurrentItem(calendar.get(Calendar.AM_PM));
    }

/*

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
*/

}
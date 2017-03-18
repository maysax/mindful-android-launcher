package co.minium.launcher3.mm;

import android.app.Fragment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.OnWheelScrollListener;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;
import co.minium.launcher3.R;
import co.minium.launcher3.app.Launcher3Prefs_;
import co.minium.launcher3.mm.model.ActivitiesStorage;
import co.minium.launcher3.mm.model.DBUtility;
import minium.co.core.ui.CoreActivity;

@EFragment(R.layout.fragment_time_picker)
public class MMTimePickerFragment extends Fragment {
    String []AmPm = new String[] {"AM", "PM"};
    boolean isTimeChanged = false;
    @Pref
    Launcher3Prefs_ launcherPrefs;

    @ViewById
    TableRow row1,row2,row3,row4;

    @ViewById
    TextView txt_total_time;

    @ViewById
    TextView txt_away;
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
        hour_horizontal.setCyclic(true);
        hour_horizontal.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                isTimeChanged = true;
                time.setText((Integer.parseInt(wheel.getCurrentItem()+"")+1)+":"+mins.getCurrentItem()+" "+AmPm[ampm.getCurrentItem()]);
            }
        });


        NumericWheelAdapter minAdapter = new NumericWheelAdapter(getActivity(), 0, 59, "%02d");
        minAdapter.setItemResource(R.layout.wheel_text_centered_dark_back);
        minAdapter.setItemTextResource(R.id.text);
        mins.setViewAdapter(minAdapter);
        mins.setCyclic(true);
        mins.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                isTimeChanged = true;
                time.setText((Integer.parseInt(hour_horizontal.getCurrentItem()+"")+1)+":"+wheel.getCurrentItem()+" "+AmPm[ampm.getCurrentItem()]);
            }
        });

        ArrayWheelAdapter<String> ampmAdapter =
                new ArrayWheelAdapter<>(getActivity(), AmPm);
        ampmAdapter.setItemResource(R.layout.wheel_text_centered_am_pm);
        ampmAdapter.setItemTextResource(R.id.text);
        ampm.setViewAdapter(ampmAdapter);
        ampm.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                isTimeChanged = true;
                time.setText((Integer.parseInt(hour_horizontal.getCurrentItem()+"")+1)+":"+mins.getCurrentItem()+" "+AmPm[wheel.getCurrentItem()]);
            }
        });

        // set current time
        Calendar calendar = Calendar.getInstance(Locale.US);
        hour_horizontal.setCurrentItem(calendar.get(Calendar.HOUR_OF_DAY));
        mins.setCurrentItem(calendar.get(Calendar.MINUTE));
        ampm.setCurrentItem(calendar.get(Calendar.AM_PM));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (launcherPrefs.isAwayChecked().get()==true){
            txt_away.setText("On");
        }else {
            txt_away.setText("Off");
        }

        String SavedTime = launcherPrefs.time().get();
        String [] timeArray = SavedTime.split(":");
        hour_horizontal.setCurrentItem(Integer.parseInt(timeArray[0])-1);
        mins.setCurrentItem(Integer.parseInt(timeArray[1]));
        ampm.setCurrentItem(Integer.parseInt(timeArray[2]));

        List<ActivitiesStorage> activitiesStorageList = DBUtility.GetActivitySession().loadAll();
        int time=0;
        for(ActivitiesStorage activitiesStorage: activitiesStorageList){
            time = time+activitiesStorage.getTime();
        }
        txt_total_time.setText("Total: "+time+" min");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("TKB Paused","Paused");

        if (isTimeChanged){
            launcherPrefs.time().put((Integer.parseInt(hour_horizontal.getCurrentItem()+"")+1)+":"+mins.getCurrentItem()+":"+ampm.getCurrentItem());
        }
        isTimeChanged = false;
    }
}
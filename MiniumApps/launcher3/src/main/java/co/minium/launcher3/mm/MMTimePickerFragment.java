package co.minium.launcher3.mm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;
import co.minium.launcher3.R;
import co.minium.launcher3.app.Launcher3Prefs_;
import co.minium.launcher3.mm.controller.AlarmController;
import co.minium.launcher3.mm.model.ActivitiesStorage;
import co.minium.launcher3.db.DBUtility;
import co.minium.launcher3.mm.model.DaysOfWeekWhichWasSetAlarm;
import co.minium.launcher3.mm.model.DaysOfWeekWhichWasSetAlarmDao;
import co.minium.launcher3.mm.model.Utilities;
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;

@EFragment(R.layout.fragment_time_picker)
public class MMTimePickerFragment extends CoreFragment {
    String []AmPm = new String[] {"AM", "PM"};
    boolean isTimeChanged = false;
    @Pref
    Launcher3Prefs_ launcherPrefs;

    @ViewById
    TableRow row1,row2,row3,row4,row5;

    @ViewById
    TextView txt_total_time,txt_alarm_days;

    @ViewById
    TextView txt_away;
    @ViewById
    ImageView crossActionBar;

    @ViewById
    TextView time;

    @ViewById
    ImageView settingsActionBar;

    @Click
    void row1(){
        ((CoreActivity)getActivity()).loadChildFragment(new AwayFragment_(),R.id.mainView);
    }
    @Click
    void row2(){
        ((CoreActivity)getActivity()).loadChildFragment(new ActivitiesFragment_(),R.id.mainView);
    }
    @Click
    void row5(){
        scheduleAlarm();
    }
    @Click
    void row4(){
        ((CoreActivity)getActivity()).loadChildFragment(new RepeatFragment_(),R.id.mainView);

    }
    @Click
    void crossActionBar(){
        getActivity().onBackPressed();
    }

    @Click
    void settingsActionBar(){
        scheduleAlarm();
    }


    public void scheduleAlarm()
    {
        // time at which alarm will be scheduled here alarm is scheduled at 1 day from current time,
        // we fetch  the current time in milliseconds and added 1 day time
        // i.e. 24*60*60*1000= 86,400,000   milliseconds in a day
        /*Log.e("TKB",": item position "+ampm.getCurrentItem());
        int timeAdjustment=0;
        if (ampm.getCurrentItem()==1){
            timeAdjustment = 12;
        }
*/
        //time.setText((Integer.parseInt(hours.getCurrentItem()+"")+1)+":"+wheel.getCurrentItem()+" "+AmPm[ampm.getCurrentItem()]);

        //Long time =Long.parseLong(((hours.getCurrentItem()+1+timeAdjustment)*mins.getCurrentItem())*1000+"");
        Calendar calendar =  Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,hours.getCurrentItem()+1);
        calendar.set(Calendar.MINUTE,mins.getCurrentItem());
        calendar.set(Calendar.SECOND,0);
       // Long time2 = new GregorianCalendar().getTimeInMillis()+1*1000;
        if (ampm.getCurrentItem()==1){
            calendar.set(Calendar.AM_PM,Calendar.PM);
        }else {
            calendar.set(Calendar.AM_PM,Calendar.AM);
        }
        if (calendar.getTime().before(Calendar.getInstance().getTime())){
            calendar.add(Calendar.DATE,1);
        }

        AlarmController.setAlarm(getActivity(),calendar);

    }
    @ViewById
    AbstractWheel hours,mins,ampm;
    @AfterViews
    void afterViews(){

        //String _hour,_minute,_amPM;
        NumericWheelAdapter hourAdapter = new NumericWheelAdapter(getActivity(), 1, 12, "%01d");
        hourAdapter.setItemResource(R.layout.wheel_text_centered);
        hourAdapter.setItemTextResource(R.id.text);
        hours.setViewAdapter(hourAdapter);
        hours.setCyclic(true);
        hours.addChangingListener(new OnWheelChangedListener() {
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
                time.setText((Integer.parseInt(hours.getCurrentItem()+"")+1)+":"+wheel.getCurrentItem()+" "+AmPm[ampm.getCurrentItem()]);
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
                time.setText((Integer.parseInt(hours.getCurrentItem()+"")+1)+":"+mins.getCurrentItem()+" "+AmPm[wheel.getCurrentItem()]);
            }
        });

        // set current time
        Calendar calendar = Calendar.getInstance(Locale.US);
        hours.setCurrentItem(calendar.get(Calendar.HOUR_OF_DAY));
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
        hours.setCurrentItem(Integer.parseInt(timeArray[0])-1);
        mins.setCurrentItem(Integer.parseInt(timeArray[1]));
        ampm.setCurrentItem(Integer.parseInt(timeArray[2]));

        List<ActivitiesStorage> activitiesStorageList = DBUtility.getActivitySession().loadAll();
        int time=0;
        for(ActivitiesStorage activitiesStorage: activitiesStorageList){
            time = time+activitiesStorage.getTime();
        }

        txt_total_time.setText("Total: "+time+" min");
        //String nameOfTheDays="";
        List<DaysOfWeekWhichWasSetAlarm> getAlarmActivatedDays = Utilities.getAlarmActivatedDays();
        List<String>name = new ArrayList<>();
        if (getAlarmActivatedDays.size()>=3){
            txt_alarm_days.setText(Utilities.multiple);
        }else {

            for (DaysOfWeekWhichWasSetAlarm alarmDays : getAlarmActivatedDays){
                name.add(alarmDays.getDay().substring(0,3));
            }

            txt_alarm_days.setText(TextUtils.join(",",name)+"");
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("TKB Paused","Paused");

        if (isTimeChanged){
            launcherPrefs.time().put((Integer.parseInt(hours.getCurrentItem()+"")+1)+":"+mins.getCurrentItem()+":"+ampm.getCurrentItem());
        }
        isTimeChanged = false;
    }
}
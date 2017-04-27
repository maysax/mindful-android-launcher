package co.minium.launcher3.mm.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import co.minium.launcher3.db.DBUtility;
import co.minium.launcher3.db.DaysOfWeekWhichWasSetAlarmDao;
import co.minium.launcher3.mm.AlarmReciever;
import co.minium.launcher3.db.DaysOfWeekWhichWasSetAlarm;
import co.minium.launcher3.mm.AlarmReciever_;

/**
 * Created by tkb on 2017-03-27.
 */

public class AlarmController {


    public static void setAlarm(Context context, Calendar calendar){


        Intent intentAlarm = new Intent(context, AlarmReciever_.class);

        // create the object
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Log.e("TKB cal: ",calendar.getTimeInMillis()+" mili: "+(new GregorianCalendar().getTimeInMillis()+1*1000));
        //set the alarm for particular time
        List<DaysOfWeekWhichWasSetAlarm> daysOfWeekWhichWasSetAlarm = DBUtility.getAlarmDaysDao().queryBuilder().where(DaysOfWeekWhichWasSetAlarmDao.Properties.IsChecked.eq(true)).list();

        if (daysOfWeekWhichWasSetAlarm!=null && daysOfWeekWhichWasSetAlarm.size()>0){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000, PendingIntent.getBroadcast(context,1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        }else {
            alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), PendingIntent.getBroadcast(context,1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        }

        Toast.makeText(context, "Alarm Set on "+ SimpleDateFormat.getDateTimeInstance().format(calendar.getTime()), Toast.LENGTH_LONG).show();

    }


}

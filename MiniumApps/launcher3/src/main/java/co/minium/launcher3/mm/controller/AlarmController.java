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

import co.minium.launcher3.mm.AlarmReciever;

/**
 * Created by tkb on 2017-03-27.
 */

public class AlarmController {

    public static void setAlarm(Context context, Calendar calendar){
        //Log.d("Time: ", SimpleDateFormat.getDateTimeInstance().format(calendar.getTime())+" time2:"+SimpleDateFormat.getDateTimeInstance().format(new Date(time2)));

        // create an Intent and set the class which will execute when Alarm triggers, here we have
        // given AlarmReciever in the Intent, the onRecieve() method of this class will execute when
        // alarm triggers and
        //we will write the code to send SMS inside onRecieve() method pf Alarmreciever class


        Intent intentAlarm = new Intent(context, AlarmReciever.class);

        // create the object
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Log.e("TKB cal: ",calendar.getTimeInMillis()+" mili: "+(new GregorianCalendar().getTimeInMillis()+1*1000));
        //set the alarm for particular time
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 60*1000, PendingIntent.getBroadcast(context,1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

        //alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), PendingIntent.getBroadcast(getActivity(),1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        Toast.makeText(context, "Alarm Set on "+ SimpleDateFormat.getDateTimeInstance().format(calendar.getTime()), Toast.LENGTH_LONG).show();

    }
}

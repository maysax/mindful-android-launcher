package co.minium.launcher3.mm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by tkb on 2017-03-21.
 */

public class AlarmReciever extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        /*String phoneNumberReciver="9718202185";// phone number to which SMS to be send
        String message="Hi I will be there later, See You soon";// message to send
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumberReciver, null, message, null, null);*/

        //EventBus.getDefault().post(new MindfulMorgingEventStart(10 * 60 * 1000));
       // DaysOfWeekWhichWasSetAlarm daysOfWeekWhichWasSetAlarm = DBUtility.getAlarmDaysDao().queryBuilder().where(DaysOfWeekWhichWasSetAlarmDao.Properties.DayValue.eq(Utilities.getDayValue())).unique();

        //if (daysOfWeekWhichWasSetAlarm!=null && daysOfWeekWhichWasSetAlarm.getIsChecked())
       /// {
            MindfulMorningActivity_.intent(context).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
        //}
        //Toast.makeText(context, "Alarm has been triggered.."+daysOfWeekWhichWasSetAlarm.getDay()+" value"+daysOfWeekWhichWasSetAlarm.getDayValue()+" DB value: "+Utilities.getDayValue(), Toast.LENGTH_LONG).show();

        //Log.e("TKB ", "Alarm has been triggered.."+daysOfWeekWhichWasSetAlarm.getDay()+" value"+daysOfWeekWhichWasSetAlarm.getDayValue()+" DB value: "+Utilities.getDayValue());
    }


/*    public void scheduleAlarm(View V)
    {
        // time at which alarm will be scheduled here alarm is scheduled at 1 day from current time,
        // we fetch  the current time in milliseconds and added 1 day time
        // i.e. 24*60*60*1000= 86,400,000   milliseconds in a day
        Long time = new GregorianCalendar().getTimeInMillis()+60*1000;

        // create an Intent and set the class which will execute when Alarm triggers, here we have
        // given AlarmReciever in the Intent, the onRecieve() method of this class will execute when
        // alarm triggers and
        //we will write the code to send SMS inside onRecieve() method pf Alarmreciever class
        Intent intentAlarm = new Intent(this, AlarmReciever.class);

        // create the object
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //set the alarm for particular time
        alarmManager.set(AlarmManager.RTC_WAKEUP,time, PendingIntent.getBroadcast(this,1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
        Toast.makeText(this, "Alarm Scheduled for Tommrrow", Toast.LENGTH_LONG).show();

    }*/

}
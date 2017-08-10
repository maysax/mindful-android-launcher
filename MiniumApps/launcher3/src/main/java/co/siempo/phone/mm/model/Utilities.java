package co.siempo.phone.mm.model;

import java.util.Calendar;
import java.util.List;

import co.siempo.phone.app.Launcher3Prefs_;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.DaysOfWeekWhichWasSetAlarm;
import co.siempo.phone.db.DaysOfWeekWhichWasSetAlarmDao;

/**
 * Created by tkb on 2017-03-24.
 */

public class Utilities {

    public static final String monday = "Mondays";
    public static final String toesday = "Tuesday";
    public static final String wednesday = "Wednesdays";
    public static final String thursday = "Thursdays";
    public static final String friday = "Fridays";
    public static final String saturday = "Saturdays";
    public static final String sunday = "Sundays";


    public static final String multiple = "Multiple";

    public static final int getDayValue() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static final Calendar getCalendar(Launcher3Prefs_ launcherPrefs) {

        String SavedTime = launcherPrefs.time().get();
        String[] timeArray = SavedTime.split(":");
        //hours.setCurrentItem(Integer.parseInt(timeArray[0])-1);
        //mins.setCurrentItem(Integer.parseInt(timeArray[1]));
        // ampm.setCurrentItem(Integer.parseInt(timeArray[2]));

        ////////////////////////////////////////////


        //time.setText((Integer.parseInt(hours.getCurrentItem()+"")+1)+":"+wheel.getCurrentItem()+" "+AmPm[ampm.getCurrentItem()]);

        //Long time =Long.parseLong(((hours.getCurrentItem()+1+timeAdjustment)*mins.getCurrentItem())*1000+"");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
        calendar.set(Calendar.SECOND, 0);
        if (Integer.parseInt(timeArray[2]) == 1) {
            calendar.set(Calendar.AM_PM, Calendar.PM);
        } else {
            calendar.set(Calendar.AM_PM, Calendar.AM);
        }
        // Long time2 = new GregorianCalendar().getTimeInMillis()+1*1000;

        if (calendar.getTime().before(Calendar.getInstance().getTime())) {
            calendar.add(Calendar.DATE, 1);
        }
        return calendar;
    }

    public static List<DaysOfWeekWhichWasSetAlarm> getAlarmActivatedDays() {
        return DBUtility.getAlarmDaysDao().queryBuilder().where(DaysOfWeekWhichWasSetAlarmDao.Properties.IsChecked.eq(true)).list();

    }
}

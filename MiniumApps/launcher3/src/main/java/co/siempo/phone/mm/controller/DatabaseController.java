package co.siempo.phone.mm.controller;

import java.util.Calendar;
import java.util.List;

import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.DaysOfWeekWhichWasSetAlarm;
import co.siempo.phone.mm.model.Utilities;

/**
 * Created by tkb on 2017-03-24.
 */

public class DatabaseController {
    public static List<DaysOfWeekWhichWasSetAlarm> getDays() {

        List<DaysOfWeekWhichWasSetAlarm> daysOfWeekCheckedList = DBUtility.getAlarmDaysDao().loadAll();
        if (daysOfWeekCheckedList.size() == 0) {
            String days[] = {Utilities.monday, Utilities.toesday, Utilities.wednesday,
                    Utilities.thursday, Utilities.friday, Utilities.saturday, Utilities.sunday};
            int dayValue[] = {Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY};
            for (int i = 0; i < days.length; i++) {

                DaysOfWeekWhichWasSetAlarm daysOfWeekWhichWasSetAlarm = new DaysOfWeekWhichWasSetAlarm();
                daysOfWeekWhichWasSetAlarm.setDay(days[i]);
                daysOfWeekWhichWasSetAlarm.setDayValue(dayValue[i]);
                daysOfWeekWhichWasSetAlarm.setIsChecked(false);
                daysOfWeekCheckedList.add(daysOfWeekWhichWasSetAlarm);
            }
            DBUtility.getAlarmDaysDao().insertInTx(daysOfWeekCheckedList);
            return daysOfWeekCheckedList;
        } else {
            return daysOfWeekCheckedList;
        }
    }

    public static void updateDaysList(List<DaysOfWeekWhichWasSetAlarm> days) {
        DBUtility.getAlarmDaysDao().updateInTx(days);

    }

}

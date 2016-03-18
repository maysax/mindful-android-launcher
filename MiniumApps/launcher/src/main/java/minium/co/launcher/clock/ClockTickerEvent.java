package minium.co.launcher.clock;

import java.util.Calendar;

/**
 * Created by shahab on 3/15/16.
 */
public class ClockTickerEvent {

    Calendar mCalendar;

    public ClockTickerEvent(Calendar mCalendar) {
        this.mCalendar = mCalendar;
    }

    public Calendar getCalendar() {
        return mCalendar;
    }
}

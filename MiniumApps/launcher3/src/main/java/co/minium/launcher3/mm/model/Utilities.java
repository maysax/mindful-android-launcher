package co.minium.launcher3.mm.model;

import java.util.Calendar;

/**
 * Created by tkb on 2017-03-24.
 */

public class Utilities {

    public static final String  monday= "Mondays";
    public static final String  toesday= "Tuesday";
    public static final String  wednesday= "Wednesdays";
    public static final String  thursday= "Thursdays";
    public static final String  friday= "Fridays";
    public static final String  saturday= "Saturdays";
    public static final String  sunday= "Sundays";

    public static final int getDayValue(){
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DAY_OF_WEEK);
    }
}

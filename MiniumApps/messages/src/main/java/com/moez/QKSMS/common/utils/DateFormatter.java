package com.moez.QKSMS.common.utils;

import android.content.Context;
import android.text.format.DateUtils;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import minium.co.messages.R;
import minium.co.messages.app.MessagesApp_;
import minium.co.messages.app.MessagesPref_;

@EBean
public class DateFormatter {

    @Pref
    MessagesPref_ prefs;

    public String getConversationTimestamp(Context context, long date) {
        if (isSameDay(date)) {
            return accountFor24HourTime(context, new SimpleDateFormat("h:mm a")).format(date);
        } else if (isSameWeek(date)) {
            return DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY);
        } else if (isSameYear(date)) {
            return DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR | DateUtils.FORMAT_ABBREV_MONTH);
        } else {
            return DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH);
        }
    }

    private boolean isSameDay(long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("D, y");
        return formatter.format(date).equals(formatter.format(System.currentTimeMillis()));
    }

    private boolean isSameWeek(long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("w, y");
        return formatter.format(date).equals(formatter.format(System.currentTimeMillis()));
    }

    private boolean isSameYear(long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("y");
        return formatter.format(date).equals(formatter.format(System.currentTimeMillis()));
    }

    private boolean isYesterday(long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yD");
        return Integer.parseInt(formatter.format(date)) + 1 == Integer.parseInt(formatter.format(System.currentTimeMillis()));
    }

    public SimpleDateFormat accountFor24HourTime(Context context, SimpleDateFormat input) { //pass in 12 hour time. If needed, change to 24 hr.
        if (prefs.isUsing24HourTime().get()) {
            return new SimpleDateFormat(input.toPattern().replace('h', 'H').replaceAll(" a", ""));
        } else return input;
    }

    public String getMessageTimestamp(Context context, long date) {
        String time = ", " + accountFor24HourTime(context, new SimpleDateFormat("h:mm a")).format(date);
        if (isSameDay(date)) {
            return accountFor24HourTime(context, new SimpleDateFormat("h:mm a")).format(date);
        } else if (isYesterday(date)) {
            return context.getString(R.string.date_yesterday) + time;
        } else if (isSameWeek(date)) {
            return DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY) + time;
        } else if (isSameYear(date)) {
            return DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR | DateUtils.FORMAT_ABBREV_MONTH) + time;
        }

        return DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH) + time;
    }

    public String getDate(Context context, long date) {
        return DateUtils.formatDateTime(context, date, DateUtils.FORMAT_SHOW_DATE) + accountFor24HourTime(context, new SimpleDateFormat(", h:mm:ss a")).format(date);
    }

    public String getRelativeTimestamp(long date) {
        String relativeTimestamp = (String) DateUtils.getRelativeTimeSpanString(date);
        if (relativeTimestamp.equals("in 0 minutes") || relativeTimestamp.equals("0 minutes ago"))
            return MessagesApp_.getInstance().getString(R.string.date_just_now);
        return relativeTimestamp;
    }

    public String getSummaryTimestamp(Context context, String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("H:mm");
        Date date;

        try {
            date = simpleDateFormat.parse(time);
            simpleDateFormat = accountFor24HourTime(context, new SimpleDateFormat("H:mm"));
            time = simpleDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }
}
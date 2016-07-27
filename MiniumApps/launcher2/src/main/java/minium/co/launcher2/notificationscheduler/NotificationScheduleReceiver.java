package minium.co.launcher2.notificationscheduler;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.orm.query.Condition;
import com.orm.query.Select;

import org.androidannotations.annotations.EReceiver;
import org.androidannotations.annotations.UiThread;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import minium.co.core.log.Tracer;
import minium.co.launcher2.model.MissedCallItem;

@EReceiver
public class NotificationScheduleReceiver extends BroadcastReceiver {
    public NotificationScheduleReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Tracer.d("NotificationScheduleReceiver onReceive: " + new SimpleDateFormat("hh:mm:ss.SSS a", Locale.US).format(new Date()));

        long count = Select.from(MissedCallItem.class)
                .where(Condition.prop("has_displayed").eq(0)).count();
        if (count > 0) {
            DisplayAlertActivity_.intent(context).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
        }
    }

    @UiThread
    void displayAlert(Context context, List<MissedCallItem> callList) {
        Tracer.d("callList: " + callList);

        for (final MissedCallItem item : callList) {
            new AlertDialog.Builder(context)
                    .setMessage(item.getNumber() + "called you at " + new SimpleDateFormat("hh:mm a", Locale.US).format(item.getDate()))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MissedCallItem.findById(MissedCallItem.class, item.getId()).setDisplayed(1).save();
                        }
                    }).show();
        }
    }
}

package minium.co.launcher2.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.orm.query.Condition;
import com.orm.query.Select;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import minium.co.core.log.Tracer;
import minium.co.core.util.UIUtils;
import minium.co.launcher2.R;
import minium.co.launcher2.model.MissedCallItem;
import minium.co.launcher2.model.ReceivedSMSItem;

@EActivity(R.layout.activity_display_alert)
public class DisplayAlertActivity extends Activity {

    private int notificationCounter;

    @AfterViews
    void afterViews() {
        List<MissedCallItem> missedCalls = Select.from(MissedCallItem.class)
                .where(Condition.prop("has_displayed").eq(0))
                .list();

        List<ReceivedSMSItem> smsItems = Select.from(ReceivedSMSItem.class)
                .where(Condition.prop("has_displayed").eq(0))
                .list();

        Tracer.d("Generating missed call notifications: " + missedCalls.size() + " and SMS: " + smsItems.size());

        if (missedCalls.size() > 0 || smsItems.size() > 0) {
            new AudioUtils().playNotificationSound(this);
        }

        if (missedCalls.size() > 0) {
            for (MissedCallItem item : missedCalls)
                showCallAlert(item.getNumber(), item.getDate());

            MissedCallItem.deleteAll(MissedCallItem.class);
        }

        if (smsItems.size() > 0) {
            for (ReceivedSMSItem item : smsItems) {
                showSMSAlert(item.getNumber(), item.getBody(), item.getDate());
            }

            ReceivedSMSItem.deleteAll(ReceivedSMSItem.class);
        }
    }

    void decreaseNotificationCounter() {
        notificationCounter--;

        if (notificationCounter == 0) {
            finish();
        }
    }

    private void showCallAlert(final String number, Date date) {
        notificationCounter++;

        UIUtils.notification(this, "Missed call " +  SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(date), "Got a missed call from " + number, R.string.label_callBack, android.R.string.cancel, R.drawable.ic_phone_missed_black_24dp, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + number));
                    startActivity(intent);
                    dialog.dismiss();
                }

                decreaseNotificationCounter();
            }
        });
    }

    private void showSMSAlert(String number, String body, Date date) {
        notificationCounter++;

        UIUtils.notification(this, "Messages " +  SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(date), "From: " + number + "\n" + body, R.string.label_showDetails, android.R.string.cancel,  R.drawable.ic_sms_black_24dp,  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("minium.co.messages", "com.moez.QKSMS.ui.MainActivity_"));
                    startActivity(intent);
                    dialog.dismiss();
                }

                decreaseNotificationCounter();
            }
        });
    }

}

package minium.co.launcher2.utils;

import android.app.Activity;

import com.orm.query.Condition;
import com.orm.query.Select;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import java.util.List;

import minium.co.core.log.Tracer;
import minium.co.launcher2.R;
import minium.co.launcher2.model.MissedCallItem;
import minium.co.launcher2.model.ReceivedSMSItem;

@EActivity(R.layout.activity_display_alert)
public class DisplayAlertActivity extends Activity {

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
            /*for (MissedCallItem item : missedCalls)
                showCallNotifications(context, item.getNumber(), missedCalls.size());
            */
            MissedCallItem.deleteAll(MissedCallItem.class);
        }

        if (smsItems.size() > 0) {
            /*for (ReceivedSMSItem item : smsItems) {
                showSMSNotifications(context, item.getNumber(), item.getBody(), smsItems.size());
            }*/

            ReceivedSMSItem.deleteAll(ReceivedSMSItem.class);
        }
    }


}

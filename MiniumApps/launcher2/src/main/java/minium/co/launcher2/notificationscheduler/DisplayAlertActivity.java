package minium.co.launcher2.notificationscheduler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.orm.query.Condition;
import com.orm.query.Select;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import minium.co.core.log.Tracer;
import minium.co.launcher2.R;
import minium.co.launcher2.model.MissedCallItem;

@EActivity(R.layout.activity_display_alert)
public class DisplayAlertActivity extends Activity {

    int count;

    @AfterViews
    void afterViews() {
        List<MissedCallItem> callList = Select.from(MissedCallItem.class)
                .where(Condition.prop("has_displayed").eq(0)).list();
        Tracer.d("callList: " + callList);

        count = callList.size();

        for (final MissedCallItem item : callList) {
            new AlertDialog.Builder(this)
                    .setMessage(item.getNumber() + " called you at " + new SimpleDateFormat("hh:mm a", Locale.US).format(item.getDate()))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MissedCallItem.findById(MissedCallItem.class, item.getId()).setDisplayed(1).save();
                            count--;

                            if (count == 0) finish();
                        }
                    }).show();
        }
    }

}

package minium.co.launcher2.notification;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import minium.co.core.log.Tracer;
import minium.co.core.util.UIUtils;
import minium.co.launcher2.R;
import minium.co.launcher2.model.MissedCallItem;
import minium.co.launcher2.model.ReceivedSMSItem;
import minium.co.launcher2.utils.AudioUtils;

@EActivity(R.layout.activity_display_alert)
public class DisplayAlertActivity extends Activity {

    @ViewById
    RecyclerView rView;

    private SiempoNotificationAdapter mAdapter;

    private int notificationCounter;

    @AfterViews
    void afterViews() {
        Tracer.d("afterViews called DisplayAlertActivity");

        loadAndFire();
    }

    private void loadAndFire() {
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

        List<SiempoNotification> notificationList = new ArrayList<>();

        if (missedCalls.size() > 0) {
            for (MissedCallItem item : missedCalls) {
                notificationList.add(new SiempoNotification(item));
                //showCallAlert(item.getNumber(), item.getDate());
            }

            MissedCallItem.deleteAll(MissedCallItem.class);
        }

        if (smsItems.size() > 0) {
            for (ReceivedSMSItem item : smsItems) {
                notificationList.add(new SiempoNotification(item));
                //showSMSAlert(item.getNumber(), item.getBody(), item.getDate());
            }

            ReceivedSMSItem.deleteAll(ReceivedSMSItem.class);
        }

        if (!notificationList.isEmpty()) {
            rView.setHasFixedSize(true);
            rView.setLayoutManager(new LinearLayoutManager(this));
            mAdapter = new SiempoNotificationAdapter(notificationList);
            mAdapter.openLoadAnimation();
            rView.addOnItemTouchListener(new OnItemChildClickListener() {
                @Override
                public void SimpleOnItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int position) {
                    SiempoNotification notification = (SiempoNotification) baseQuickAdapter.getItem(position);
                    if (notification.isMissedCallItem()) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + notification.getMissedCallItem().getNumber()));
                        startActivity(intent);
                    } else {
                        String defaultApplication = Settings.Secure.getString(getContentResolver(), "sms_default_application");
                        PackageManager pm = getPackageManager();
                        Intent intent = pm.getLaunchIntentForPackage(defaultApplication);
                        if (intent != null) {
                            startActivity(intent);
                        }
                    }
                    baseQuickAdapter.remove(position);
                    baseQuickAdapter.notifyItemRemoved(position);

                    if (baseQuickAdapter.getItemCount() == 0) {
                        finish();
                    }
                }
            });
            rView.setAdapter(mAdapter);
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

        UIUtils.notification(this, "Missed call " +  SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(date), "Got a missed call from " + number, R.string.label_callBack, R.string.label_dismiss, R.drawable.ic_phone_missed_black_24dp, new DialogInterface.OnClickListener() {
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

        UIUtils.notification(this, "Messages " +  SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(date), "From: " + number + "\n" + body, R.string.label_view, R.string.label_dismiss,  R.drawable.ic_sms_black_24dp,  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setComponent(new ComponentName("minium.co.messages", "com.moez.QKSMS.ui.MainActivity_"));
                    String defaultApplication = Settings.Secure.getString(getContentResolver(), "sms_default_application");
                    PackageManager pm = getPackageManager();
                    Intent intent = pm.getLaunchIntentForPackage(defaultApplication );
                    if (intent != null) {
                        startActivity(intent);
                    }

                    dialog.dismiss();
                }

                decreaseNotificationCounter();
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tracer.d("onNewIntent received in DisplayAlertActivity");
        loadAndFire();
    }
}

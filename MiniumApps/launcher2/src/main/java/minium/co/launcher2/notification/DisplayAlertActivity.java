package minium.co.launcher2.notification;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.orm.query.Condition;
import com.orm.query.Select;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.util.ThemeUtils;
import minium.co.core.util.UIUtils;
import minium.co.launcher2.R;
import minium.co.launcher2.flow.SiempoNotificationService_;
import minium.co.launcher2.model.MissedCallItem;
import minium.co.launcher2.model.ReceivedSMSItem;
import minium.co.launcher2.ui.TopFragment_;
import minium.co.launcher2.utils.AudioUtils;
import minium.co.launcher2.utils.RecyclerViewItemClickListener;
import minium.co.launcher2.utils.VibrationUtils;

@EActivity(R.layout.activity_display_alert)
public class DisplayAlertActivity extends CoreActivity implements RecyclerViewItemClickListener {

    @ViewById
    FrameLayout statusView;

    @ViewById
    RecyclerView rView;

    @Bean
    VibrationUtils vibration;

    private SiempoNotificationAdapter mAdapter;

    private List<SiempoNotification> notificationList;

    private int notificationCounter;

    @AfterViews
    void afterViews() {
        Tracer.d("afterViews called DisplayAlertActivity");
        statusView.setBackgroundColor(ThemeUtils.getPrimaryDarkColor(this));
        loadTopView();

        loadAndFire();
    }

    void loadTopView() {
        loadFragment(TopFragment_.builder().build(), R.id.statusView, "status");
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
            alertUser();
        }

        notificationList = new ArrayList<>();

        if (missedCalls.size() > 0) {
            for (MissedCallItem item : missedCalls) {
                notificationList.add(new SiempoNotification(item));
                //showCallAlert(item.getNumber(), item.getDate());
            }

            //MissedCallItem.deleteAll(MissedCallItem.class);
        }

        if (smsItems.size() > 0) {
            for (ReceivedSMSItem item : smsItems) {
                notificationList.add(new SiempoNotification(item));
                //showSMSAlert(item.getNumber(), item.getBody(), item.getDate());
            }

            //ReceivedSMSItem.deleteAll(ReceivedSMSItem.class);
        }

        if (!notificationList.isEmpty()) {
            rView.setHasFixedSize(true);
            rView.setLayoutManager(new LinearLayoutManager(this));
            mAdapter = new SiempoNotificationAdapter(notificationList, this);
            rView.setAdapter(mAdapter);
        }
    }

    @UiThread(delay = 2000)
    void enableService() {
        if (prefs.isNotificationSchedulerEnabled().get()) {
            SiempoNotificationService_.intent(this).extra("start", true).start();
        }
    }

    void disableService() {
        if (prefs.isNotificationSchedulerEnabled().get()) {
            SiempoNotificationService_.intent(this).extra("start", false).start();
        }
    }

    @UiThread(delay = 1000)
    void fireNotification() {
        new AudioUtils().playNotificationSound(DisplayAlertActivity.this);
        vibration.vibrate();
    }

    private void alertUser() {
        Tracer.d("Alerting user ..");
        disableService();
        fireNotification();
        enableService();
    }

    void decreaseNotificationCounter() {
        notificationCounter--;

        if (notificationCounter == 0) {
            finish();
        }
    }

    private void showCallAlert(final String number, Date date) {
        notificationCounter++;

        UIUtils.notification(this, "Missed call " + SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(date), "Got a missed call from " + number, R.string.label_callBack, R.string.label_dismiss, R.drawable.ic_phone_missed_black_24dp, new DialogInterface.OnClickListener() {
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

        UIUtils.notification(this, "Messages " + SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(date), "From: " + number + "\n" + body, R.string.label_view, R.string.label_dismiss, R.drawable.ic_sms_black_24dp, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setComponent(new ComponentName("minium.co.messages", "com.moez.QKSMS.ui.MainActivity_"));
                    String defaultApplication = Settings.Secure.getString(getContentResolver(), "sms_default_application");
                    PackageManager pm = getPackageManager();
                    Intent intent = pm.getLaunchIntentForPackage(defaultApplication);
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
        Tracer.v("onNewIntent received in DisplayAlertActivity");
        loadAndFire();
    }

    @Override
    public void onItemClick(int id, View view, int position) {
        Tracer.d("SimpleOnItemChildClick called for position: " + position);
        SiempoNotification notification = notificationList.get(position);

        switch (view.getId()) {
            case R.id.notificationAction:

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
                break;
        }

        if (notification.isMissedCallItem()) {
            MissedCallItem missedCallItem = MissedCallItem.findById(MissedCallItem.class, notification.getMissedCallItem().getId());
            MissedCallItem.delete(missedCallItem);
        } else {
            ReceivedSMSItem smsItem = ReceivedSMSItem.findById(ReceivedSMSItem.class, notification.getSmsItem().getId());
            ReceivedSMSItem.delete(smsItem);
        }

        notificationList.remove(position);
        mAdapter.notifyItemRemoved(position);
        if (mAdapter.getItemCount() == 0) {
            finish();
        }
    }
}

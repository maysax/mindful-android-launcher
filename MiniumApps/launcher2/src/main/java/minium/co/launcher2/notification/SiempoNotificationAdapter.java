package minium.co.launcher2.notification;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.text.SimpleDateFormat;
import java.util.List;

import minium.co.launcher2.R;

/**
 * Created by Shahab on 10/11/2016.
 */

public class SiempoNotificationAdapter extends BaseQuickAdapter<SiempoNotification> {

    public SiempoNotificationAdapter(List<SiempoNotification> data) {
        super(R.layout.item_notification, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, SiempoNotification siempoNotification) {
        if (siempoNotification.isMissedCallItem()) {
            baseViewHolder
                    .setImageResource(R.id.notificationAvatar, R.drawable.ic_phone_missed_black_24dp)
                    .setText(R.id.notificationTitle, siempoNotification.getMissedCallItem().getNumber())
                    .setVisible(R.id.notificationText, false)
                    .setText(R.id.notificationDate, SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(siempoNotification.getMissedCallItem().getDate()))
                    .setText(R.id.notificationAction, R.string.label_callBack)
                    .addOnClickListener(R.id.notificationAction);
        } else {
            baseViewHolder
                    .setImageResource(R.id.notificationAvatar, R.drawable.ic_sms_black_24dp)
                    .setText(R.id.notificationTitle, siempoNotification.getSmsItem().getNumber())
                    .setText(R.id.notificationText, siempoNotification.getSmsItem().getBody())
                    .setText(R.id.notificationDate, SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(siempoNotification.getSmsItem().getDate()))
                    .setText(R.id.notificationAction, R.string.label_view)
                    .addOnClickListener(R.id.notificationAction);
        }

    }
}

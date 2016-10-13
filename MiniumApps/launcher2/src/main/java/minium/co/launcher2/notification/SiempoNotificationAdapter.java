package minium.co.launcher2.notification;

import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.text.SimpleDateFormat;
import java.util.List;

import minium.co.launcher2.R;
import minium.co.launcher2.utils.RecyclerViewItemClickListener;

/**
 * Created by Shahab on 10/11/2016.
 */

public class SiempoNotificationAdapter extends RecyclerView.Adapter<SiempoNotificationAdapter.ViewHolder> {

    private List<SiempoNotification> data;
    private RecyclerViewItemClickListener listener;

    public SiempoNotificationAdapter(List<SiempoNotification> data, RecyclerViewItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(inflatedView, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SiempoNotification notification = data.get(position);
        if (notification.isMissedCallItem()) {
            holder.imgNotificationAvatar.setImageResource(R.drawable.ic_phone_missed_black_24dp);
            holder.textNotificationTitle.setText(notification.getMissedCallItem().getNumber());
            holder.textNotification.setVisibility(View.GONE);
            holder.textDate.setText(SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(notification.getMissedCallItem().getDate()));
            holder.btnNotificationAction.setText(R.string.label_callBack);
            holder.btnNotificationDismiss.setText(R.string.label_dismiss);


        } else {
            holder.imgNotificationAvatar.setImageResource(R.drawable.ic_sms_black_24dp);
            holder.textNotificationTitle.setText(notification.getSmsItem().getNumber());
            holder.textNotification.setText(notification.getSmsItem().getBody());
            holder.textDate.setText(SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(notification.getSmsItem().getDate()));
            holder.btnNotificationAction.setText(R.string.label_view);
            holder.btnNotificationDismiss.setText(R.string.label_dismiss);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgNotificationAvatar;
        TextView textNotificationTitle;
        TextView textNotification;
        TextView textDate;
        AppCompatButton btnNotificationAction;
        AppCompatButton btnNotificationDismiss;

        RecyclerViewItemClickListener listener;

        public ViewHolder(View itemView, RecyclerViewItemClickListener listener) {
            super(itemView);
            this.listener = listener;
            this.imgNotificationAvatar = (ImageView) itemView.findViewById(R.id.notificationAvatar);
            this.textNotificationTitle = (TextView) itemView.findViewById(R.id.notificationTitle);
            this.textNotification = (TextView) itemView.findViewById(R.id.notificationText);
            this.textDate = (TextView) itemView.findViewById(R.id.notificationDate);
            this.btnNotificationAction = (AppCompatButton) itemView.findViewById(R.id.notificationAction);
            this.btnNotificationDismiss = (AppCompatButton) itemView.findViewById(R.id.dismissAction);

            this.btnNotificationAction.setOnClickListener(this);
            this.btnNotificationDismiss.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v.getId(), v, getLayoutPosition());
        }
    }
}

/*
 * Copyright (C) 2015 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.siempo.phone.notification;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Telephony;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.app.Constants;
import co.siempo.phone.db.NotificationSwipeEvent;
import co.siempo.phone.main.ItemTouchHelperAdapter;
import co.siempo.phone.main.ItemTouchHelperViewHolder;
import co.siempo.phone.main.OnStartDragListener;
import co.siempo.phone.notification.remove_notification_strategy.DeleteItem;
import co.siempo.phone.notification.remove_notification_strategy.SingleIteamDelete;
import de.greenrobot.event.EventBus;
import minium.co.core.app.CoreApplication;
import minium.co.core.util.UIUtils;


public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private Context mContext;
    private List<Notification> notificationList;
    //= new ArrayList<>();

    private final OnStartDragListener mDragStartListener;
    private String defSMSApp;

    public RecyclerListAdapter(Context context, List<Notification> notificationList, OnStartDragListener dragStartListener) {
        mContext = context;
        mDragStartListener = dragStartListener;
        this.notificationList = notificationList;
        defSMSApp = Telephony.Sms.getDefaultSmsPackage(mContext);
    }


    public RecyclerListAdapter(Context context, List<Notification> notificationList) {
        mContext = context;
        mDragStartListener = null;
        this.notificationList = notificationList;
        Log.d("Test", "notificationList" + notificationList.size());
        defSMSApp = Telephony.Sms.getDefaultSmsPackage(mContext);
    }

    public void updateReceiptsList(List<Notification> newlist) {
        notificationList.clear();
        notificationList.addAll(newlist);
        notifyDataSetChanged();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_card, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        if (notification.getNotificationType() == NotificationUtility.NOTIFICATION_TYPE_EVENT) {
            Bitmap bitmap = CoreApplication.getInstance().iconList.get(notification.getPackageName());
            holder.imgAppIcon.setImageBitmap(bitmap);
            holder.txtAppName.setText(CoreApplication.getInstance().getApplicationNameFromPackageName(notification.getPackageName()));
            if (notification.getStrTitle().equalsIgnoreCase("")) {
                holder.txtUserName.setText("");
                holder.txtUserName.setVisibility(View.GONE);
                holder.txtMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            } else {
                holder.txtUserName.setText(notification.getStrTitle());
                holder.txtUserName.setVisibility(View.VISIBLE);
                holder.txtMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            }

            holder.txtMessage.setText(notification.get_text());
            holder.txtTime.setText(notification.get_time());
            holder.imgUserImage.setVisibility(View.GONE);
            if (notification.getPackageName().equalsIgnoreCase(Constants.FACEBOOK_MESSENGER_PACKAGE)
                    || notification.getPackageName().equalsIgnoreCase(Constants.WHATSAPP_PACKAGE)
                    || notification.getPackageName().equalsIgnoreCase(Constants.FACEBOOK_LITE_PACKAGE)
                    || notification.getPackageName().equalsIgnoreCase(Constants.FACEBOOK_PACKAGE)
                    || notification.getPackageName().equalsIgnoreCase(Constants.GOOGLE_HANGOUTS_PACKAGES)) {
                holder.imgUserImage.setVisibility(View.VISIBLE);
                if (notification.getUser_icon() != null) {
                    holder.imgUserImage.setImageBitmap(UIUtils.convertBytetoBitmap(notification.getUser_icon()));
                }else{
                    holder.imgUserImage.setBackground(null);
                }
            }

        } else {
            holder.txtUserName.setText(notification.getNotificationContactModel().getName());
            if (notification.get_text().equalsIgnoreCase(mContext.getString(R.string.missed_call))) {
                holder.imgAppIcon.setBackground(null);
                holder.imgAppIcon.setImageDrawable(mContext.getResources().getDrawable(android.R.drawable.sym_call_missed, null));
                holder.txtAppName.setText(R.string.phone);
            } else {
                holder.imgAppIcon.setBackground(null);
                holder.txtAppName.setText(CoreApplication.getInstance().getApplicationNameFromPackageName(defSMSApp));
                holder.imgAppIcon.setImageBitmap(CoreApplication.getInstance().iconList.get(defSMSApp));
            }
            holder.txtMessage.setText(notification.get_text());
            holder.txtTime.setText(notification.get_time());
            try {
                if (notification.getNotificationContactModel().getImage() != null && !notification.getNotificationContactModel().getImage().equals("")) {
                    Glide.with(mContext)
                            .load(Uri.parse(notification.getNotificationContactModel().getImage()))
                            .placeholder(R.drawable.ic_person_black_24dp)
                            .into(holder.imgUserImage);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onItemDismiss(int position) {
        //++Tarun following code will delete this item form database
        try {
            if (notificationList != null && notificationList.get(position) != null) {
                DeleteItem deleteItem = new DeleteItem(new SingleIteamDelete());
                deleteItem.executeDelete(notificationList.get(position));
                notificationList.remove(position);
                notifyItemRemoved(position);
                if (notificationList.isEmpty())
                    EventBus.getDefault().post(new NotificationSwipeEvent(true));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(notificationList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {
        ImageView imgAppIcon, imgUserImage;
        TextView txtAppName, txtTime, txtUserName, txtMessage;

        ItemViewHolder(View view) {
            super(view);
            imgAppIcon = view.findViewById(R.id.imgAppIcon);
            imgUserImage = view.findViewById(R.id.imgUserImage);
            txtAppName = view.findViewById(R.id.txtAppName);
            txtTime = view.findViewById(R.id.txtTime);
            txtUserName = view.findViewById(R.id.txtUserName);
            txtMessage = view.findViewById(R.id.txtMessage);

        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}

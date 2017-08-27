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
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.event.TopBarUpdateEvent;
import co.siempo.phone.main.ItemTouchHelperAdapter;
import co.siempo.phone.main.ItemTouchHelperViewHolder;
import co.siempo.phone.main.OnStartDragListener;
import co.siempo.phone.notification.remove_notification_strategy.DeleteIteam;
import co.siempo.phone.notification.remove_notification_strategy.SingleIteamDelete;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;


public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private Context mContext;
    private List<Notification> notificationList = new ArrayList<>();

    private final OnStartDragListener mDragStartListener;

    public RecyclerListAdapter(Context context, List<Notification> notificationList, OnStartDragListener dragStartListener) {
        mContext = context;
        mDragStartListener = dragStartListener;
        this.notificationList = notificationList;
        System.out.println("Notification fragment calling adapter");
    }


    public RecyclerListAdapter(Context context, List<Notification> notificationList) {
        mContext = context;
        mDragStartListener = null;
        this.notificationList = notificationList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_card, parent, false);

        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder._name.setText(notification.getNotificationContactModel().getName());
        holder._text.setText(notification.get_text());
        holder._time.setText(notification.get_time());
        //holder.thumbnail.setImageResource(notification.get_image());
        try {
            if (notification.getNotificationContactModel().getImage() != null && !notification.getNotificationContactModel().getImage().equals("")) {
                Glide.with(mContext)
                        .load(Uri.parse(notification.getNotificationContactModel().getImage()))
                        .placeholder(R.drawable.ic_person_black_24dp)
                        .into(holder.thumbnail);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //  }
//        Glide.with(mContext).load(notification.getNotificationContactModel().getImage()).into(holder.thumbnail);
    }

    @Override
    public void onItemDismiss(int position) {
        //++Tarun following code will delete this item form database
        DeleteIteam deleteIteam = new DeleteIteam(new SingleIteamDelete());
        deleteIteam.executeDelete(notificationList.get(position));

        notificationList.remove(position);
        notifyItemRemoved(position);

        if (notificationList.isEmpty())
            EventBus.getDefault().post(new TopBarUpdateEvent());
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
    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {
        public TextView _name, _text, _time;
        public ImageView overflow;
        public CircleImageView thumbnail;

        public ItemViewHolder(View view) {
            super(view);
            _name = (TextView) view.findViewById(R.id.text_name_notification);
            _text = (TextView) view.findViewById(R.id.text_mesage_notification);
            _time = (TextView) view.findViewById(R.id.text_time_notification);
            thumbnail = (CircleImageView) view.findViewById(R.id.thumbnail_notification);
            overflow = (ImageView) view.findViewById(R.id.image_checked_notification);
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

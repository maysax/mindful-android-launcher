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

package co.minium.launcher3.notification;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.minium.launcher3.R;
import co.minium.launcher3.main.ItemTouchHelperAdapter;
import co.minium.launcher3.main.ItemTouchHelperViewHolder;
import co.minium.launcher3.main.OnStartDragListener;
import co.minium.launcher3.msg.SmsEvent;
import co.minium.launcher3.msg.SmsEventType;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;


public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private Context mContext;
    private List<Notification> notificationList = new ArrayList<>();

    private final OnStartDragListener mDragStartListener;

    public RecyclerListAdapter(Context context,List<Notification> notificationList, OnStartDragListener dragStartListener) {
        mContext = context;
        mDragStartListener = dragStartListener;
        this.notificationList = notificationList;
        System.out.println("Notification fragment calling adapter");
    }


    public RecyclerListAdapter(Context context,List<Notification> notificationList) {
        mContext = context;
        mDragStartListener = null;
        this.notificationList = notificationList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_card,parent, false);

        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        Notification notification  = notificationList.get(position);
        holder._name.setText(notification.get_name());
        holder._text.setText(notification.get_text());
        holder._time.setText(notification.get_time());
        holder.thumbnail.setImageResource(notification.get_image());
    }

    @Override
    public void onItemDismiss(int position) {
        notificationList.remove(position);
        notifyItemRemoved(position);

        if (notificationList.isEmpty())
            EventBus.getDefault().post(new SmsEvent(SmsEventType.CANCEL));
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
        public TextView _name, _text,_time;
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

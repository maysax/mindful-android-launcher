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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import co.minium.launcher3.MainActivity;
import co.minium.launcher3.R;
import co.minium.launcher3.main.OnStartDragListener;
import co.minium.launcher3.main.SimpleItemTouchHelperCallback;
import minium.co.core.ui.CoreFragment;

public class NotificationFragment extends CoreFragment implements OnStartDragListener {

    private ItemTouchHelper mItemTouchHelper;
    private List<Notification> notificationList;

    public NotificationFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.notification_main,container,false);
   //     return new RecyclerView(container.getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        notificationList = new ArrayList<>();
        prepareNotifications();
        RecyclerListAdapter adapter = new RecyclerListAdapter(getActivity(), notificationList,this);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void prepareNotifications() {

        Notification n = new Notification("Jaineel Shah","Haha. Sure! 7.", R.drawable.ic_person_black_24dp  ,"12:52 pm",false);
        notificationList.add(n);
        n = new Notification("Stephanie Wise","Excellent example of the",R.drawable.ic_person_black_24dp  ,"12:45 pm",false);
        notificationList.add(n);
        n = new Notification("Hilah Lucida","Good call, I'll do the same",R.drawable.ic_person_black_24dp  ,"12:31 pm",false);
        notificationList.add(n);

        //      adapter.notifyDataSetChanged();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.isNotificationTrayVisible = false;
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {

    }
}

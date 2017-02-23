package co.minium.launcher3.notification;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import co.minium.launcher3.R;

/**
 * Created by itc on 17/02/17.
 */
public class NotificationActivity extends Activity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_main);
        this.setTitle("Notifications");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        notificationList = new ArrayList<>();

        adapter = new NotificationAdapter(this,notificationList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        prepareNotifications();


    }

    private void prepareNotifications() {

        Notification n = new Notification("Jaineel Shah","Haha. Sure! 7.",R.drawable.ic_person_black_24dp  ,"12:52 pm",false);
        notificationList.add(n);

        n = new Notification("Stephanie Wise","Excellent example of the",R.drawable.ic_person_black_24dp  ,"12:45 pm",false);
        notificationList.add(n);

        n = new Notification("Hilah Lucida","Good call, I'll do the same",R.drawable.ic_person_black_24dp  ,"12:31 pm",false);
        notificationList.add(n);


        adapter.notifyDataSetChanged();


    }
}

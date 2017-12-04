package co.siempo.phone.settings;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import co.siempo.phone.R;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.notification.Notification;
import co.siempo.phone.notification.NotificationContactModel;
import co.siempo.phone.notification.NotificationUtility;
import co.siempo.phone.notification.RecyclerListAdapter;
import co.siempo.phone.notification.remove_notification_strategy.DeleteItem;
import co.siempo.phone.notification.remove_notification_strategy.MultipleIteamDelete;

public class SiempoSupressNotificationActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    Context context;
    private TextView txtClearAll,emptyView;
    private TableNotificationSmsDao smsDao;
    List<Notification> notificationList;
    private RecyclerListAdapter adapter;
    SharedPreferences launcherPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_siempo_supress_notification);
        initView();
    }

    public void initView(){
        context = SiempoSupressNotificationActivity.this;
        launcherPrefs = getSharedPreferences("Launcher3Prefs", 0);
        txtClearAll = findViewById(R.id.txtClearAll);
        emptyView = findViewById(R.id.emptyView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        notificationList = new ArrayList<>();
        adapter = new RecyclerListAdapter(context, notificationList);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        smsDao = DBUtility.getNotificationDao();
        loadData();

        txtClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteItem deleteItem = new DeleteItem(new MultipleIteamDelete());
                deleteItem.deleteAll();
                notificationList.clear();
                adapter.notifyDataSetChanged();
                txtClearAll.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        });
    }


    private void loadData() {
        List<TableNotificationSms> SMSItems = smsDao.queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();
        if(SMSItems.size() == 0){
            txtClearAll.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else{

            emptyView.setVisibility(View.GONE);
            txtClearAll.setVisibility(View.VISIBLE);
        }
        setUpNotifications(SMSItems);
    }


    private void setUpNotifications(List<TableNotificationSms> items) {
        notificationList.clear();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Enable permission from Settings to access Contact details.", Toast.LENGTH_SHORT).show();
        }

        for (int i = 0; i < items.size(); i++) {

            DateFormat sdf = new SimpleDateFormat(getTimeFormat(context));
            String time = sdf.format(items.get(i).get_date());
            if (items.get(i).getNotification_type() == NotificationUtility.NOTIFICATION_TYPE_EVENT) {
                Notification notification = new Notification();
                notification.setId(items.get(i).getId());
                notification.setNotitification_date(items.get(i).getNotification_date());
                notification.setNotificationType(items.get(i).getNotification_type());
                notification.setApp_icon(items.get(i).getApp_icon());
                notification.setUser_icon(items.get(i).getUser_icon());
                notification.setPackageName(items.get(i).getPackageName());
                notification.set_time(time);
                notification.setStrTitle(items.get(i).get_contact_title());
                notification.set_text(items.get(i).get_message());
                notificationList.add(notification);
            } else {
                Notification n = new Notification(gettingNameAndImageFromPhoneNumber(items.get(i).get_contact_title()), items.get(i).getId(), items.get(i).get_contact_title(), items.get(i).get_message(), time, false, items.get(i).getNotification_type());
                notificationList.add(n);
            }
        }



    }

    public String getTimeFormat(Context context){
        String format="";
        boolean is24hourformat=android.text.format.DateFormat.is24HourFormat(context);

        if(is24hourformat){
            format="HH:mm";
        }
        else{
            format="hh:mm a";
        }
        return format;
    }



    private NotificationContactModel gettingNameAndImageFromPhoneNumber(String number) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            Cursor cursor = context.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.PHOTO_URI}, null, null, null);

            String contactName, imageUrl = "";
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                    imageUrl = cursor
                            .getString(cursor
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                    cursor.close();

                } else {
                    contactName = number;
                }
            } catch (Exception e) {
                contactName = "";
                imageUrl = "";
                e.printStackTrace();
            }


            NotificationContactModel notificationContactModel = new NotificationContactModel();
            notificationContactModel.setName(contactName);
            notificationContactModel.setImage(imageUrl);
            return notificationContactModel;
        }
        return null;
    }
}

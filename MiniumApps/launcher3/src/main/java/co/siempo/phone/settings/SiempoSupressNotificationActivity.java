package co.siempo.phone.settings;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.helper.ActivityHelper;
import co.siempo.phone.notification.ItemClickSupport;
import co.siempo.phone.notification.Notification;
import co.siempo.phone.notification.NotificationContactModel;
import co.siempo.phone.notification.NotificationUtility;
import co.siempo.phone.notification.RecyclerListAdapter;
import co.siempo.phone.notification.remove_notification_strategy.DeleteItem;
import co.siempo.phone.notification.remove_notification_strategy.MultipleIteamDelete;
import minium.co.core.app.CoreApplication;

public class SiempoSupressNotificationActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    Context context;
    private TextView txtClearAll,emptyView;
    private TableNotificationSmsDao smsDao;
    List<Notification> notificationList= new ArrayList<>();
    List<Notification> suggetionList = new ArrayList<>();
    private RecyclerListAdapter adapter;
    SharedPreferences launcherPrefs;
    private EditText edt_search;
    public static final String TAG = SiempoSupressNotificationActivity.class.getName();
    ArrayList<String> disableNotificationApps= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        suggetionList = new ArrayList<>();
        edt_search= findViewById(R.id.edt_search);
        edt_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                suggetionList.clear();
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                suggetionList.clear();
                for(int i =0 ;i<notificationList.size();i++){
                    if(!TextUtils.isEmpty(notificationList.get(i).getPackageName())) {
                        if (CoreApplication.getInstance().getApplicationNameFromPackageName(notificationList.get(i).getPackageName()).toLowerCase().startsWith(s.toString().toLowerCase())) {
                            suggetionList.add(notificationList.get(i));
                        }
                        if (suggetionList.size() == 0) {
                            txtClearAll.setVisibility(View.GONE);
                        } else {
                            txtClearAll.setVisibility(View.VISIBLE);
                        }
                    }
                  adapter = new RecyclerListAdapter(context, suggetionList);
                  recyclerView.setAdapter(adapter);
                }

            }
        });

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
                adapter = new RecyclerListAdapter(context, notificationList);
                recyclerView.setAdapter(adapter);
                edt_search.setVisibility(View.GONE);
                txtClearAll.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        });


        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {

            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                 // Get notification object from
                 //  1. SuggetionList - if user has search in suggestion box
                 //  2. NotificationList - Get whole enable app notificationlist, if suggestion box is empty
                Notification notification = null;
                if(!TextUtils.isEmpty(edt_search.getText().toString().trim())){
                    if(suggetionList.size() > position && suggetionList.get(position)!=null){
                        notification = suggetionList.get(position);
                    }
                }
                else{
                    if(notificationList.size() > position && notificationList.get(position)!=null){
                        notification = notificationList.get(position);
                    }
                }
                if (notification !=null) {
                    if (notification.getNotificationType() == NotificationUtility.NOTIFICATION_TYPE_SMS) {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", notification.getNumber(), null));
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                        // Following code will delete all notification of same user and same types.
                        DeleteItem deleteItem = new DeleteItem(new MultipleIteamDelete());
                        deleteItem.executeDelete(notification);
                        loadData();
                    } else if (notification.getNotificationType() == NotificationUtility.NOTIFICATION_TYPE_CALL) {
                        if (
                                ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                                        && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {

                        } else {
                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + notification.getNumber()));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                        // Following code will delete all notification of same user and same types.
                        DeleteItem deleteItem = new DeleteItem(new MultipleIteamDelete());
                        deleteItem.executeDelete(notification);
                        loadData();
                    } else {
                        String strPackageName = notification.getPackageName();
                        List<TableNotificationSms> tableNotificationSms = DBUtility.getNotificationDao().queryBuilder()
                                .where(TableNotificationSmsDao.Properties.PackageName.eq(notification.getPackageName())).list();
                        DBUtility.getNotificationDao().deleteInTx(tableNotificationSms);
                        if(!TextUtils.isEmpty(edt_search.getText().toString().trim())){

                            suggetionList.remove(position);
                            adapter = new RecyclerListAdapter(context, suggetionList);
                            recyclerView.setAdapter(adapter);
                        }
                        else{

                            notificationList.remove(position);
                            adapter = new RecyclerListAdapter(context, notificationList);
                            recyclerView.setAdapter(adapter);
                        }
                        loadData();
                        new ActivityHelper(context).openAppWithPackageName(strPackageName);
                    }
                    if(edt_search!=null){
                        edt_search.getText().clear();
                    }
                }
            }


        });
    }


    private void loadData() {

        String disable_AppList=launcherPrefs.getString(CoreApplication.getInstance().DISABLE_APPLIST,"");
        if(!TextUtils.isEmpty(disable_AppList)){
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
            disableNotificationApps = new Gson().fromJson(disable_AppList, type);
        }


        List<TableNotificationSms> SMSItems = smsDao.queryBuilder().orderDesc(TableNotificationSmsDao.Properties.Notification_date).build().list();

        Log.d(TAG,"Store Notifications size :: "+SMSItems.size());
        setUpNotifications(SMSItems);
    }


    private void setUpNotifications(List<TableNotificationSms> items) {
        notificationList.clear();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Enable permission from Settings to access Contact details.", Toast.LENGTH_SHORT).show();
        }

        for (int i = 0; i < items.size(); i++) {
            if(!disableNotificationApps.contains(items.get(i).getPackageName())) {
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
                    Notification n = new Notification(gettingNameAndImageFromPhoneNumber(items.get(i).get_contact_title()), items.get(i).getId(), items.get(i).get_contact_title(), items.get(i).get_message(), time, false, items.get(i).getNotification_type(),items.get(i).getPackageName());
                    notificationList.add(n);
                }
            }

            adapter = new RecyclerListAdapter(context, notificationList);
            recyclerView.setAdapter(adapter);


        }

        if(notificationList.size() == 0){
            edt_search.setVisibility(View.GONE);
            txtClearAll.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else{
            edt_search.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            txtClearAll.setVisibility(View.VISIBLE);
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

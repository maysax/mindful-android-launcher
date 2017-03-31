package co.minium.launcher3.notification;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.eyeem.chips.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Touch;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.greendao.query.Query;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.minium.launcher3.MainActivity;
import co.minium.launcher3.MainActivity_;
import co.minium.launcher3.R;
import co.minium.launcher3.app.Launcher3App;
import co.minium.launcher3.call.DaoSession;
import co.minium.launcher3.db.TableNotificationSms;
import co.minium.launcher3.db.TableNotificationSmsDao;
import co.minium.launcher3.main.MainFragment_;
import co.minium.launcher3.main.OnStartDragListener;
import co.minium.launcher3.main.SimpleItemTouchHelperCallback;
import co.minium.launcher3.mm.model.ActivitiesStorageDao;
import de.greenrobot.event.Subscribe;
import minium.co.core.app.CoreApplication;
import minium.co.core.event.CheckActivityEvent;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.DateUtils;
import minium.co.core.util.UIUtils;

/**
 * Created by itc on 17/02/17.
 */
@EFragment(R.layout.notification_main)
public class NotificationFragment extends CoreFragment{

    private static final String TAG = "NotificationFragment";

    @ViewById
    RecyclerView recyclerView;

    RecyclerListAdapter adapter;
    private List<Notification> notificationList;


    private enum mSwipeDirection{UP,DOWN,NONE};

    TableNotificationSmsDao smsDao;

    @AfterViews
    void afterViews() {

        notificationList = new ArrayList<>();
        DaoSession daoSession = ((Launcher3App)CoreApplication.getInstance()).getDaoSession();
        smsDao = daoSession.getTableNotificationSmsDao();


//      adapter = new NotificationAdapter(this,notificationList);
        prepareNotifications();

        // .queryBuilder().where(ActivitiesStorageDao.Properties.Time.notEq(0)).list();
        // query all notes, sorted a-z by their text
//        smsQuery = smsDao.queryBuilder().orderAsc(TableNotificationSmsDao.Properties._contact_title).build();

        List<TableNotificationSms> items = smsDao.queryBuilder().orderDesc(TableNotificationSmsDao.Properties._date).build().list();
        //List<TableNotificationSms> items = smsDao.loadAll();
        setUpNotifications(items);

        adapter = new RecyclerListAdapter(getActivity(),notificationList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);


        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter,getActivity());
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);


        recyclerView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return gesture.onTouchEvent(event);
            }
        });


        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {

            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // do it
                Toast.makeText(getActivity().getApplicationContext(), "Item clicked at position "+ notificationList.get(position).getNotificationContactModel().getImage(), Toast.LENGTH_SHORT).show();
            }


        });

        ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
               Toast.makeText(getActivity().getApplicationContext(), "Item long clicked at position "+ position, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }

    private void setUpNotifications(List<TableNotificationSms> items) {

        for(int i = 0; i < items.size(); i++){

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a");
            String time = sdf.format(items.get(i).get_date());
            Notification n = new Notification(gettingNameAndImageFromPhoneNumber(items.get(i).get_contact_title()),items.get(i).get_message(),time,false);
            notificationList.add(n);
        }
    }
    private NotificationContactModel gettingNameAndImageFromPhoneNumber(String number){
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = getActivity().getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI},null,null,null);

        String contactName,imageUrl="";
        if(cursor != null && cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            imageUrl = cursor
                    .getString(cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            cursor.close();

        }else {
            contactName = number;
        }

        NotificationContactModel notificationContactModel = new NotificationContactModel();
        notificationContactModel.setName(contactName);
        notificationContactModel.setImage(imageUrl);

        return notificationContactModel;
    }
/*

    private String gettingNameFromPhoneNumber(String number){
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,, Uri.encode(number));
        Cursor cursor = getActivity().getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,ContactsContract.PhoneLookup._ID},null,null,null);

        String contactName,contactId;
        Bitmap photo;
        if(cursor != null && cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));

            try {
                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactId)));

                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream);
                }else {
                    photo = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.ic_person_black_24dp);
                }

                assert inputStream != null;
                inputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            contactName = number;
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }
*/
    private Bitmap retrieveContactPhoto(String number) {
        ContentResolver contentResolver = context.getContentResolver();
        String contactId = null;
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

        Cursor cursor =
                contentResolver.query(
                        uri,
                        projection,
                        null,
                        null,
                        null);

        if(cursor != null && cursor.moveToFirst()) {
                contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                cursor.close();
        }

        Bitmap photo = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_person_black_24dp);

        try {
            if (contactId!=null){
                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactId)));

                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream);
                }

                assert inputStream != null;
                inputStream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return photo;
    }
    final GestureDetector gesture = new GestureDetector(getActivity(),
            new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                       float velocityY) {
                    Log.i(TAG, "onFling has been called!");
                    final int SWIPE_MIN_DISTANCE = 120;
                    final int SWIPE_MAX_OFF_PATH = 250;
                    final int SWIPE_THRESHOLD_VELOCITY = 200;
                    try {
                        if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH)
                            return false;
                        if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
                                && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                            Log.i(TAG, "Down to Top");
                            animateOut();

                        } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
                                && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                            Log.i(TAG, "Top to Down");
                        }
                    } catch (Exception e) {
                        // nothing
                    }
                    return super.onFling(e1, e2, velocityX, velocityY);
                }
            });

    public void animateOut()
    {
        TranslateAnimation trans=new TranslateAnimation(0,0,0,-300* UIUtils.getDensity(getActivity()));
        trans.setDuration(500);
        trans.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                getActivity().getFragmentManager().beginTransaction().remove(NotificationFragment.this).commit();
            }
        });
        getView().startAnimation(trans);
    }




    private void prepareNotifications() {

//        Notification n = new Notification("Jaineel Shah","Haha. Sure! 7.",R.drawable.ic_person_black_24dp  ,"12:52 pm",false);
//        notificationList.add(n);
//
//        n = new Notification("Stephanie Wise","Excellent example of the",R.drawable.ic_person_black_24dp  ,"12:45 pm",false);
//        notificationList.add(n);
//
//        n = new Notification("Hilah Lucida","Good call, I'll do the same",R.drawable.ic_person_black_24dp  ,"12:31 pm",false);
//        notificationList.add(n);

  //      adapter.notifyDataSetChanged();
/*
        TableNotificationSms smsNoti = new TableNotificationSms();
        smsNoti.set_contact_id(1);
        smsNoti.set_contact_title("Jaineel Shah");
        smsNoti.set_message("Haha. Sure! 7 :-)");
        smsNoti.set_sms_id(1);
        smsNoti.set_date(new Date());
        smsNoti.set_snooze_time(30l);
        smsNoti.set_is_read(false);
        smsDao.insert(smsNoti);
        Log.d(TAG, "Inserted new sms noti , ID: " + smsNoti.getId());

        smsNoti = new TableNotificationSms();
        smsNoti.set_contact_id(2);
        smsNoti.set_contact_title("Leigh Wasson");
        smsNoti.set_message("Thank you");
        smsNoti.set_sms_id(2);
        smsNoti.set_date(new Date());
        smsNoti.set_snooze_time(30l);
        smsNoti.set_is_read(false);
        smsDao.insert(smsNoti);
        Log.d(TAG, "Inserted new sms noti , ID: " + smsNoti.getId());*/


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        StatusBarHandler.isNotificationTrayVisible = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            UIUtils.hideSoftKeyboard(getActivity(),getActivity().getCurrentFocus().getWindowToken());
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
    }



    //    @Override
//    protected void onResume() {
//        super.onResume();
//        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
//    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mDownX = ev.getX();
//                mDownY = ev.getY();
//                mSwipe = mSwipeDirection.NONE;
//                mSwiping = false;
//                break;
//            case MotionEvent.ACTION_CANCEL:
//            case MotionEvent.ACTION_UP:
//                if(mSwiping) {
//                    swipeScreen(mSwipe); //if action recognized as swipe then swipe
//
//                }
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float x = ev.getX();
//                float y = ev.getY();
//                float xDelta = Math.abs(x - mDownX);
//                float yDelta = Math.abs(y - mDownY);
//                if(mDownY-y<=100)
//                    mSwipe = mSwipeDirection.DOWN;
//                else if(mDownY - y > 100)
//                    mSwipe = mSwipeDirection.UP;
//                else
//                    mSwipe = mSwipeDirection.NONE;
//
//                if (yDelta > mSlop && yDelta / 2 > xDelta) {
//                    mSwiping = true;
//                    return true;
//                }
//                break;
//        }
//
//
//        return super.dispatchTouchEvent(ev);
//    }

    private void swipeScreen(mSwipeDirection mSwipe) {
        //if(mSwipe == mSwipeDirection.UP)
        //finish();
    }

}

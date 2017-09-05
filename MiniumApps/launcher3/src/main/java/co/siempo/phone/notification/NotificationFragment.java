package co.siempo.phone.notification;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import co.siempo.phone.R;
import co.siempo.phone.db.CallStorageDao;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.NotificationSwipeEvent;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.event.NotificationTrayEvent;
import co.siempo.phone.event.TopBarUpdateEvent;
import co.siempo.phone.main.SimpleItemTouchHelperCallback;
import co.siempo.phone.notification.remove_notification_strategy.DeleteIteam;
import co.siempo.phone.notification.remove_notification_strategy.MultipleIteamDelete;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import minium.co.core.config.Config;
import minium.co.core.event.HomePressEvent;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;


@EFragment(R.layout.notification_main)
public class NotificationFragment extends CoreFragment implements View.OnTouchListener {

    private static final String TAG = "NotificationFragment";

    @ViewById
    RecyclerView recyclerView;

    @ViewById
    TextView emptyView;

    RecyclerListAdapter adapter;
    private List<Notification> notificationList;

    @ViewById
    LinearLayout layout_notification;

    @ViewById
    LinearLayout linSecond;

//    @ViewById
//    Button btnClearAll;

    @Subscribe
    public void homePressEvent(HomePressEvent event) {
        try {
            animateOut();
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
    }

    @SuppressLint("LogConditional")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "" + event.getAction());
        return false;
    }

    private enum mSwipeDirection {UP, DOWN, NONE}

    TableNotificationSmsDao smsDao;
    CallStorageDao callStorageDao;
    int count = 1;

    @AfterViews
    void afterViews() {

        notificationList = new ArrayList<>();
        recyclerView.setNestedScrollingEnabled(false);

        smsDao = DBUtility.getNotificationDao();
        callStorageDao = DBUtility.getCallStorageDao();
        loadData();

        adapter = new RecyclerListAdapter(getActivity(), notificationList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);


        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter, getActivity());
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateOut();
            }
        });
        linSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linSecond.setClickable(false);
                animateOut();
            }
        });

        recyclerView.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("LogConditional")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean isShown = false;
                if (linSecond != null) {
                    isShown = linSecond.isShown();
                }
                if (isShown) {
                    int yStart = 0;
                    int yEnd = 0;
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            yStart = (int) event.getY();
                            break;
                        case MotionEvent.ACTION_UP:
                            yEnd = (int) event.getY();
                            break;
                    }
//                    if (isLastItemDisplaying(recyclerView) && Math.abs(yStart - yEnd) > 300) {
//                        animateOut();
//                    }
                }
                return gesture.onTouchEvent(event);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isLastItemDisplaying(recyclerView)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            count++;
                        }
                    }, 500);

                    Log.d(TAG, "" + count);
                }

            }
        });


        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {

            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // do it
                // Toast.makeText(getActivity().getApplicationContext(), "Item clicked at position " + notificationList.get(position).getNotificationContactModel().getImage(), Toast.LENGTH_SHORT).show();
                if (notificationList.get(position).getNotificationType() == NotificationUtility.NOTIFICATION_TYPE_SMS) {

                   /* Uri uri = Uri.parse("smsto:"+notificationList.get(position).getNotificationContactModel().getNumber());
                    Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                    startActivity(it);*/
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", notificationList.get(position).getNumber(), null)));

                } else if (notificationList.get(position).getNotificationType() == NotificationUtility.NOTIFICATION_TYPE_CALL) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + notificationList.get(position).getNumber()));
                    startActivity(intent);
                }
                //++Tarun , Following code will delete all notification of same user and same types.
                DeleteIteam deleteIteam = new DeleteIteam(new MultipleIteamDelete());
                deleteIteam.executeDelete(notificationList.get(position));
                loadData();
            }


        });

        // This feature included in feature sprint.
//        ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
//                Toast.makeText(getActivity().getApplicationContext(), "Item long clicked at position " + position, Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });

    }

    private void loadData() {
        List<TableNotificationSms> SMSItems = smsDao.queryBuilder().orderDesc(TableNotificationSmsDao.Properties._date).build().list();
        //List<CallStorage> callItems = callStorageDao.queryBuilder().orderDesc(CallStorageDao.Properties._date).build().list();

        //List<TableNotificationSms> items = smsDao.loadAll();
        setUpNotifications(SMSItems);
        EventBus.getDefault().post(new TopBarUpdateEvent());
    }


    private void setUpNotifications(List<TableNotificationSms> items) {

        for (int i = 0; i < items.size(); i++) {
            //DateFormat dateFormat = new SimpleDateFormat("hh:mm a");

            @SuppressLint("SimpleDateFormat") DateFormat sdf = new SimpleDateFormat("hh:mm a");
            String time = sdf.format(items.get(i).get_date());
            Notification n = new Notification(gettingNameAndImageFromPhoneNumber(items.get(i).get_contact_title()), items.get(i).getId(), items.get(i).get_contact_title(), items.get(i).get_message(), time, false, items.get(i).getNotification_type());
            notificationList.add(n);
        }

        if (items.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            //  btnClearAll.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            //  btnClearAll.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

    }

    private NotificationContactModel gettingNameAndImageFromPhoneNumber(String number) {

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = getActivity().getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
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
                    final int SWIPE_MIN_DISTANCE = 30;
                    final int SWIPE_MAX_OFF_PATH = 250;
                    final int SWIPE_THRESHOLD_VELOCITY = 200;

                    try {
                        if (e1 != null && e2 != null) {
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
                        }
                    } catch (Exception e) {
                        // nothing
                        e.printStackTrace();
                    }
                    return super.onFling(e1, e2, velocityX, velocityY);
                }
            });

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            if (recyclerView.getAdapter().getItemCount() != 0) {
                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                    return true;
            }
            count = 1;
        }
        return false;
    }

    public void animateOut() {
        TranslateAnimation trans = new TranslateAnimation(0, 0, 0, -500 * UIUtils.getDensity(getActivity()));
        trans.setFillAfter(true);
        trans.setDuration(500);
        trans.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    linSecond.setClickable(true);
                    EventBus.getDefault().post(new NotificationTrayEvent(false));
//                    getActivity().getFragmentManager().popBackStack();
                    getActivity().getFragmentManager().beginTransaction().remove(NotificationFragment.this).commit();
                    Config.isNotificationAlive = false;
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent("IsNotificationVisible").putExtra("IsNotificationVisible", false));
                } catch (Exception e) {
                    Tracer.e(e, e.getMessage());
                }
            }
        });
        if (getView() != null) {
            getView().startAnimation(trans);
        }
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
            //noinspection ConstantConditions
            if (getActivity() != null)
                UIUtils.hideSoftKeyboard(getActivity(), getActivity().getCurrentFocus().getWindowToken());
        } catch (Exception e) {
            Tracer.e(e, e.getMessage());
        }
    }

    /**
     * This method is called from adapter when there is no notification reamaning in list.
     * @param event
     */
    @Subscribe
    public void notificationSwipeEvent(NotificationSwipeEvent event) {
        try {
            if (event.isNotificationListNull()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                //    btnClearAll.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                //  btnClearAll.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void swipeScreen(mSwipeDirection mSwipe) {
        //if(mSwipe == mSwipeDirection.UP)
        //finish();
    }

}

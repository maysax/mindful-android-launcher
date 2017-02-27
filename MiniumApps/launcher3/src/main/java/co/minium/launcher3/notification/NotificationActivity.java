package co.minium.launcher3.notification;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.telecom.Call;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import java.util.ArrayList;
import java.util.List;

import co.minium.launcher3.MainActivity_;
import co.minium.launcher3.R;
import co.minium.launcher3.main.GestureListener;
import co.minium.launcher3.main.OnStartDragListener;
import co.minium.launcher3.main.SimpleItemTouchHelperCallback;

/**
 * Created by itc on 17/02/17.
 */
public class NotificationActivity extends Activity implements OnStartDragListener{

    private static final String TAG = "NotificationActivity";

    private RecyclerView recyclerView;
//   private NotificationAdapter adapter;
    RecyclerListAdapter adapter;
    private List<Notification> notificationList;


    private ItemTouchHelper mItemTouchHelper;

    private int mSlop;
    private float mDownX;
    private float mDownY;
    private boolean mSwiping;

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
       // mItemTouchHelper.startDrag(viewHolder);
    }

    private enum mSwipeDirection{UP,DOWN,NONE};

    private mSwipeDirection mSwipe = mSwipeDirection.NONE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_main);

/*        ColorDrawable colorDrawable = new ColorDrawable( Color.TRANSPARENT );
        getWindow().setBackgroundDrawable( colorDrawable );*/

        ViewConfiguration vc = ViewConfiguration.get(this);
        mSlop = vc.getScaledTouchSlop();


        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        notificationList = new ArrayList<>();

//      adapter = new NotificationAdapter(this,notificationList);
        prepareNotifications();
        adapter = new RecyclerListAdapter(this,notificationList,this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);


        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);




    }

    private void prepareNotifications() {

        Notification n = new Notification("Jaineel Shah","Haha. Sure! 7.",R.drawable.ic_person_black_24dp  ,"12:52 pm",false);
        notificationList.add(n);

        n = new Notification("Stephanie Wise","Excellent example of the",R.drawable.ic_person_black_24dp  ,"12:45 pm",false);
        notificationList.add(n);

        n = new Notification("Hilah Lucida","Good call, I'll do the same",R.drawable.ic_person_black_24dp  ,"12:31 pm",false);
        notificationList.add(n);


  //      adapter.notifyDataSetChanged();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity_.isNotificationTrayVisible = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
      //  overridePendingTransition(0,R.anim.abc_slide_out_top);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);

    }

    @Override
    protected void onPause() {
        super.onPause();

        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                mSwipe = mSwipeDirection.NONE;
                mSwiping = false;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if(mSwiping) {
                    swipeScreen(mSwipe); //if action recognized as swipe then swipe

                }
                break;
            case MotionEvent.ACTION_MOVE:
                float x = ev.getX();
                float y = ev.getY();
                float xDelta = Math.abs(x - mDownX);
                float yDelta = Math.abs(y - mDownY);
                if(mDownY-y<=100)
                    mSwipe = mSwipeDirection.DOWN;
                else if(mDownY - y > 100)
                    mSwipe = mSwipeDirection.UP;
                else
                    mSwipe = mSwipeDirection.NONE;

                if (yDelta > mSlop && yDelta / 2 > xDelta) {
                    mSwiping = true;
                    return true;
                }
                break;
        }


        return super.dispatchTouchEvent(ev);
    }

    private void swipeScreen(mSwipeDirection mSwipe) {
        if(mSwipe == mSwipeDirection.UP)
        finish();
    }
}

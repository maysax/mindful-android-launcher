package co.minium.launcher3.notification;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
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
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.Touch;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import co.minium.launcher3.MainActivity;
import co.minium.launcher3.MainActivity_;
import co.minium.launcher3.R;
import co.minium.launcher3.main.MainFragment_;
import co.minium.launcher3.main.OnStartDragListener;
import co.minium.launcher3.main.SimpleItemTouchHelperCallback;
import de.greenrobot.event.Subscribe;
import minium.co.core.event.CheckActivityEvent;
import minium.co.core.log.Tracer;
import minium.co.core.ui.CoreActivity;
import minium.co.core.ui.CoreFragment;
import minium.co.core.util.UIUtils;

/**
 * Created by itc on 17/02/17.
 */
@EFragment(R.layout.notification_main)
public class NotificationFragment extends CoreFragment{

    private static final String TAG = "NotificationFragment";

    @ViewById
    RecyclerView recyclerView;
//   private NotificationAdapter adapter;
    RecyclerListAdapter adapter;
    private List<Notification> notificationList;


    private ItemTouchHelper mItemTouchHelper;

    private int mSlop;
    private float mDownX;
    private float mDownY;
    private boolean mSwiping;

    private enum mSwipeDirection{UP,DOWN,NONE};

    private mSwipeDirection mSwipe = mSwipeDirection.NONE;

  //  private  StatusBarHandler statusBarHandler;

    @AfterViews
    void afterViews() {

        ViewConfiguration vc = ViewConfiguration.get(getActivity());
        mSlop = vc.getScaledTouchSlop();

        notificationList = new ArrayList<>();

//      adapter = new NotificationAdapter(this,notificationList);
        prepareNotifications();
        adapter = new RecyclerListAdapter(getActivity(),notificationList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);


        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter,getActivity());
        mItemTouchHelper = new ItemTouchHelper(callback);
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
                Toast.makeText(getActivity().getApplicationContext(), "Item clicked at position "+ position, Toast.LENGTH_SHORT).show();
            }
        });

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

        Notification n = new Notification("Jaineel Shah","Haha. Sure! 7.",R.drawable.ic_person_black_24dp  ,"12:52 pm",false);
        notificationList.add(n);

        n = new Notification("Stephanie Wise","Excellent example of the",R.drawable.ic_person_black_24dp  ,"12:45 pm",false);
        notificationList.add(n);

        n = new Notification("Hilah Lucida","Good call, I'll do the same",R.drawable.ic_person_black_24dp  ,"12:31 pm",false);
        notificationList.add(n);

  //      adapter.notifyDataSetChanged();
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

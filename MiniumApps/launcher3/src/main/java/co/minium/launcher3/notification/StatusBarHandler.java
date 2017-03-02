package co.minium.launcher3.notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import co.minium.launcher3.MainActivity;

/**
 * Created by itc on 02/03/17.
 */

public class StatusBarHandler {


    private String TAG = "StatusBarHandler";


    public static boolean isNotificationTrayVisible = false;
    private Context mContext;
    protected static customViewGroup blockingView;
    private int status_bar_height = 0;

    public StatusBarHandler(Context context) {
        mContext = context;
        blockingView = new customViewGroup(context);
    }

    public  void requestStatusBarCustomization(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(mContext)) {
                Toast.makeText(mContext, TAG +  " User can access system settings without this permission!", Toast.LENGTH_SHORT).show();
                preventStatusBarExpansion();
            }
            else
            {
                preventStatusBarExpansion();
            }
        }else
            preventStatusBarExpansion();
    }

    // preventStatusBarExpansion

    private void preventStatusBarExpansion() {
        System.out.println(TAG + " preventStatusBarExpansion");
        WindowManager manager = ((WindowManager) mContext.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));

        Activity activity = (Activity)mContext;
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|

                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        int resId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = 0;
        if (resId > 0) {
            result = activity.getResources().getDimensionPixelSize(resId);
        }

        status_bar_height = result;

        localLayoutParams.height = result;

        localLayoutParams.format = PixelFormat.TRANSPARENT;

        manager.addView(blockingView, localLayoutParams);
    }

    private class customViewGroup extends ViewGroup {


        public customViewGroup(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
        }


        @Override
        public boolean onTouchEvent(MotionEvent event) {

            if(event.getY() > status_bar_height){
                if(!isNotificationTrayVisible)
                {
                    System.out.println(TAG + " y position on Touch on notification tray "+ event.getY() + "status_bar_height " + status_bar_height);
                    Intent intent = new Intent(mContext, NotificationActivity.class);
                   mContext. startActivity(intent);

                    isNotificationTrayVisible = true;
                }
            }


            return super.onTouchEvent(event);
        }

    }

    /*
    Added so that when not in launcher it allow status bar to default state
     */


    public void restoreStatusBarExpansion(){
        System.out.println(TAG + " restoreStatusBarExpansion");
        if(blockingView!=null)
            System.out.println(TAG + " restoreStatusBarExpansion  token == " + blockingView.getWindowToken());
        if(blockingView!=null)
            if (blockingView.getWindowToken()!=null) {
                WindowManager manager = ((WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
                manager.removeView(blockingView);
                System.out.println(TAG + " restored StatusBar Expansion");
            }else
            {
                System.out.println(TAG + " restoreStatusBarExpansion got null ");
            }
    }
}

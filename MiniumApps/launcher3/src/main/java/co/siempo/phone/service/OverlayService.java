package co.siempo.phone.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import co.siempo.phone.R;

/**
 * Created by roma on 23/4/18.
 */

public class OverlayService extends Service {

    private WindowManager wm;
    private View androidHead;
    private Handler handler;
    private int delay;
    private int heightWindow;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            androidHead = ((LayoutInflater) getSystemService(Context
                    .LAYOUT_INFLATER_SERVICE)).inflate(R.layout
                    .gray_scale_layout, null);
            wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = 200;
            params.gravity = Gravity.BOTTOM;

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            } else {
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            }

            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

            params.format = PixelFormat.TRANSLUCENT;

            if (null != wm) {
                wm.addView(androidHead, params);
            }
            handler = new Handler();
            //milliseconds
            delay = 1000;
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            heightWindow = size.y * 6 / 9;
            handler.postDelayed(new Runnable() {
                public void run() {
                    try {
                        if (params.height <= heightWindow) {
                            //Increase height of overlay
                            params.height = params.height + 100;
                            androidHead.setLayoutParams(new ViewGroup.LayoutParams(params));
                            wm.updateViewLayout(androidHead, params);
                            handler.postDelayed(this, delay);
                        } else {
                            handler.removeCallbacksAndMessages(null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, delay);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        removeView();
    }

    private void removeView() {
        try {
            if (androidHead != null && wm != null) {
                wm.removeView(androidHead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

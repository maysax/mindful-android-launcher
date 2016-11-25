package com.itconquest.tracking.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.SystemService;

import minium.co.core.log.Tracer;

/**
 * Created by Shahab on 11/25/2016.
 */
@EService
public class GlobalTouchService extends Service implements View.OnTouchListener {

    @SystemService
    WindowManager windowManager;

    LinearLayout dummyView;

    public GlobalTouchService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        dummyView = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT);
        dummyView.setLayoutParams(params);
        dummyView.setOnTouchListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams(
                1, /* width */
                1, /* height */
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSPARENT
        );
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        windowManager.addView(dummyView, wmParams);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Tracer.d("Touch event: " + event.toString());
        return false;
    }
}

package com.siempo.tracking.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.siempo.tracking.receivers.ScreenOnOffReceiver_;

import org.androidannotations.annotations.EService;

/**
 * Created by Shahab on 11/24/2016.
 */
@EService
public class ScreenOnOffService extends Service {

    ScreenOnOffReceiver_ receiver = new ScreenOnOffReceiver_();

    @Override
    public void onCreate() {
        super.onCreate();
        // REGISTER RECEIVER THAT HANDLES SCREEN ON AND SCREEN OFF LOGIC
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            boolean screenOn = intent.getBooleanExtra("screen_state", false);
            if (!screenOn) {
                // YOUR CODE
            } else {
                // YOUR CODE
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

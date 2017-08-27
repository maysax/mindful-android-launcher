package com.siempo.tracking.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.siempo.tracking.receivers.HomePressReceiver_;

import org.androidannotations.annotations.EService;

/**
 * Created by Shahab on 1/5/2017.
 */
@EService
public class HomePressService extends Service {

    HomePressReceiver_ receiver = new HomePressReceiver_();

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(receiver, intentFilter);
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

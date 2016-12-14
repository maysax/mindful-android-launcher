package com.itconquest.tracking;

import android.app.ActivityManager;

import com.itconquest.tracking.util.TrackingLogger;

import org.androidannotations.annotations.EApplication;

import minium.co.core.app.CoreApplication;

/**
 * Created by Shahab on 11/24/2016.
 */

@EApplication
public class App extends CoreApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        TrackingLogger.log("Tracking app started", null);
    }
}

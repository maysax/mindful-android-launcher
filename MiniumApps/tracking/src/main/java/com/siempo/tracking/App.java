package com.siempo.tracking;

import android.provider.Settings;

import com.siempo.tracking.model.LogEvent;
import com.siempo.tracking.util.TrackingLogger;
import com.siempo.tracking.util.TrackingPref_;

import org.androidannotations.annotations.EApplication;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import minium.co.core.app.CoreApplication;

/**
 * Created by Shahab on 11/24/2016.
 */

@EApplication
public class App extends CoreApplication {

    @Pref
    TrackingPref_ prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        //TrackingLogger.log("Tracking app started", null);
        TrackingLogger.log(new LogEvent(LogEvent.EventType.STARTED));
    }

    public String getFileName() {
        if (prefs.trackingLogFileName().get().isEmpty()) {
            prefs.trackingLogFileName().put(generateFileName());
        }

        return prefs.trackingLogFileName().get();
    }

    private String generateFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd'T'HHmmssZ", Locale.US);
        sdf.setTimeZone(TimeZone.getDefault());

        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return deviceId
                + "_" + sdf.format(Calendar.getInstance().getTime())
                + "_" + BuildConfig.VERSION_NAME + ".txt";
    }
}

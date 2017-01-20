package com.siempo.tracking.util;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by Shahab on 1/6/2017.
 */
@SharedPref(SharedPref.Scope.UNIQUE)
public interface TrackingPref {

    @DefaultBoolean(false)
    boolean isTrackingRunning();

    @DefaultString("")
    String trackingLogFileName();

}

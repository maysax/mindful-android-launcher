package co.minium.launcher3.app;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by Shahab on 2/16/2017.
 */
@SharedPref(SharedPref.Scope.UNIQUE)
public interface Launcher3Prefs {

    @DefaultBoolean(false)
    boolean isPauseActive();

    @DefaultBoolean(false)
    boolean isPauseAllowFavoriteChecked();

    @DefaultBoolean(false)
    boolean isPauseAllowCallsChecked();

    @DefaultBoolean(false)
    boolean isNotificationBlockerServiceRunning();

    @DefaultBoolean(false)
    boolean isTempoActive();

    @DefaultBoolean(false)
    boolean isAwayChecked();

    @DefaultString("10:20:1")
    String time();

    @DefaultString("")
    String awayMessage();
}

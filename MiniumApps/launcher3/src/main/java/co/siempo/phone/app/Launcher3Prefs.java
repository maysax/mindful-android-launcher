package co.siempo.phone.app;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultLong;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

import static co.siempo.phone.app.Constants.DEFAULT_TEMPO_MINUTE;

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

    // Tempo related settings

    @DefaultBoolean(false)
    boolean isTempoActive();

    @DefaultLong(0)
    long tempoNextNotificationMillis();

    @DefaultBoolean(false)
    boolean tempoAllowFavorites();

    @DefaultBoolean(false)
    boolean tempoAllowCalls();

    @DefaultInt(DEFAULT_TEMPO_MINUTE)
    int tempoIntervalMinutes();

    @DefaultBoolean(false)
    boolean isAwayChecked();

    @DefaultString("10:20:1")
    String time();

    @DefaultString("")
    String awayMessage();
}

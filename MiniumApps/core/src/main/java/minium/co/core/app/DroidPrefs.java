package minium.co.core.app;

import android.graphics.Color;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultFloat;
import org.androidannotations.annotations.sharedpreferences.DefaultInt;
import org.androidannotations.annotations.sharedpreferences.DefaultLong;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by Shahab on 6/2/2016.
 */
@SharedPref(SharedPref.Scope.UNIQUE)
public interface DroidPrefs {

    @DefaultInt(Color.BLACK)
    int selectedThemeColor();

    @DefaultInt(0)
    int selectedThemeId();

    @DefaultInt(0)
    int notificationScheduleIndex();

    @DefaultInt(0)
    int notificationSchedulerValue();

    @DefaultLong(0)
    long notificationScheulerNextMillis();

    @DefaultBoolean(true)
    boolean notificationSchedulerSupressCalls();

    @DefaultBoolean(true)
    boolean notificationSchedulerSupressSMS();

    // Flow related configurations
    @DefaultBoolean(false)
    boolean isFlowRunning();

    @DefaultFloat(0)
    float flowMaxTimeLimitMillis();

    @DefaultFloat(0)
    float flowSegmentDurationMillis();

    // Notification Scheduler configurations

    /**
     * true - Notification Scheduler could not display scheduled notification due to other prioritized action (i.e. Flow)
     * false - otherwise
     * @return
     */
    @DefaultBoolean(false)
    boolean isNotificationSupressed();

    @DefaultBoolean(false)
    boolean isNotificationSchedulerEnabled();

    @DefaultBoolean(false)
    boolean isSiempoNotificationServiceRunning();

    @DefaultBoolean(false)
    boolean hasShownIntroScreen();
}

package minium.co.core.util;

import de.greenrobot.event.EventBus;
import minium.co.core.event.CheckActivityEvent;

/**
 * Created by itc on 02/03/17.
 */

public class ActiveActivitiesTracker {
    private static int sActiveActivities = 0;

    public static void activityStarted() {
        if (sActiveActivities == 0) {
            // TODO: Here is presumably "application level" resume
            EventBus.getDefault().post(new CheckActivityEvent(sActiveActivities, true));
        }
        sActiveActivities++;
    }

    public static void activityStopped() {
        sActiveActivities--;
        if (sActiveActivities == 0) {
            // TODO: Here is presumably "application level" pause
            EventBus.getDefault().post(new CheckActivityEvent(sActiveActivities, false));
        }
    }
}

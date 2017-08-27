package minium.co.core.event;

/**
 * Created by Shahab on 1/5/2017.
 */

public class CheckActivityEvent {
    private int activityCount;
    private boolean isResume;

    public CheckActivityEvent(int activityCount, boolean isResume) {
        this.activityCount = activityCount;
        this.isResume = isResume;
    }

    public int getActivityCount() {
        return activityCount;
    }

    public boolean isResume() {
        return isResume;
    }
}

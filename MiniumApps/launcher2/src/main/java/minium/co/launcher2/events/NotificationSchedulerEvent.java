package minium.co.launcher2.events;

/**
 * Created by Shahab on 8/12/2016.
 */
public class NotificationSchedulerEvent {

    private boolean isEnabled;

    public NotificationSchedulerEvent(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}

package co.minium.launcher3.event;

/**
 * Created by Shahab on 8/12/2016.
 */
public class NotificationTrayEvent {

    private boolean isVisible;

    public NotificationTrayEvent(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public boolean isVisible() {
        return isVisible;
    }
}

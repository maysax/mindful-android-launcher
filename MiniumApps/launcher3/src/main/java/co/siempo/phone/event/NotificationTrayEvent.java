package co.siempo.phone.event;

/**
 * Created by Shahab on 8/12/2016.
 */
@SuppressWarnings("ALL")
public class NotificationTrayEvent {

    private boolean isVisible;

    public NotificationTrayEvent(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public boolean isVisible() {
        return isVisible;
    }
}

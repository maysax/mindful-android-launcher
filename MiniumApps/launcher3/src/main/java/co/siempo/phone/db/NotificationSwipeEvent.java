package co.siempo.phone.db;

/**
 * Created by rajeshjadi on 4/9/17.
 * This class is used when user clear notification from adapter class.
 */

public class NotificationSwipeEvent {
    private boolean isNotificationListNull = false;

    public NotificationSwipeEvent(boolean isNotificationListNull) {
        this.isNotificationListNull = isNotificationListNull;
    }

    public void setNotificationListNull(boolean notificationListNull) {
        isNotificationListNull = notificationListNull;
    }

    public boolean isNotificationListNull() {
        return isNotificationListNull;
    }
}

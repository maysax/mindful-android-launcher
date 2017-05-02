package co.siempo.phone.notification.remove_notification_strategy;

import co.siempo.phone.notification.Notification;

/**
 * Created by tkb on 2017-04-03.
 */

public interface DeleteStrategy {
    void delete(Notification notification);
}

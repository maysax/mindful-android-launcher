package co.minium.launcher3.notification.remove_notification_strategy;

import co.minium.launcher3.notification.Notification;

/**
 * Created by tkb on 2017-04-03.
 */

public interface DeleteStrategy {
    void delete(Notification notification);
}

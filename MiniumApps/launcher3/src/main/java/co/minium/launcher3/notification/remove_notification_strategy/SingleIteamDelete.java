package co.minium.launcher3.notification.remove_notification_strategy;

import java.util.List;

import co.minium.launcher3.db.DBUtility;
import co.minium.launcher3.db.TableNotificationSms;
import co.minium.launcher3.db.TableNotificationSmsDao;
import co.minium.launcher3.notification.Notification;

/**
 * Created by tkb on 2017-04-03.
 */

public class SingleIteamDelete implements DeleteStrategy {
    @Override
    public void delete(Notification notification) {
        TableNotificationSms notificationSms = DBUtility.getNotificationDao().queryBuilder()
                .where(TableNotificationSmsDao.Properties._contact_title.eq(notification.getNumber()),
                        TableNotificationSmsDao.Properties.Notification_type.eq(notification.getNotificationType()),
                        TableNotificationSmsDao.Properties.Id.eq(notification.getId()))
                .unique();

        DBUtility.getNotificationDao().delete(notificationSms);
    }
}

package co.siempo.phone.notification.remove_notification_strategy;

import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.notification.Notification;
import co.siempo.phone.notification.NotificationUtility;

/**
 * Created by tkb on 2017-04-03.
 */


public class SingleIteamDelete implements DeleteStrategy {
    @Override
    public void delete(Notification notification) {
        try {
            TableNotificationSms notificationSms = null;
            if (notification.getNotificationType() == NotificationUtility.NOTIFICATION_TYPE_EVENT) {
                notificationSms = DBUtility.getNotificationDao().queryBuilder()
                        .where(TableNotificationSmsDao.Properties.Id.eq(notification.getId()))
                        .unique();
            } else {
                 notificationSms = DBUtility.getNotificationDao().queryBuilder()
                        .where(TableNotificationSmsDao.Properties._contact_title.eq(notification.getNumber()),
                                TableNotificationSmsDao.Properties.Notification_type.eq(notification.getNotificationType()),
                                TableNotificationSmsDao.Properties.Id.eq(notification.getId()))
                        .unique();
            }
            if (notificationSms != null) {
                DBUtility.getNotificationDao().delete(notificationSms);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAll() {

    }
}

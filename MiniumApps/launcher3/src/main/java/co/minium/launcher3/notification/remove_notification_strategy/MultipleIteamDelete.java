package co.minium.launcher3.notification.remove_notification_strategy;

import java.util.List;

import co.minium.launcher3.db.DBUtility;
import co.minium.launcher3.db.TableNotificationSms;
import co.minium.launcher3.db.TableNotificationSmsDao;
import co.minium.launcher3.notification.Notification;

/**
 * Created by tkb on 2017-04-03.
 */

public class MultipleIteamDelete implements DeleteStrategy {
    @Override
    public void delete(Notification notification) {
        List<TableNotificationSms> notificationSmsesList = DBUtility.getNotificationDao().queryBuilder()
                .where(TableNotificationSmsDao.Properties._contact_title.eq(notification.getNumber()),
                TableNotificationSmsDao.Properties.Notification_type.eq(notification.getNotificationType()))
                .list();

        DBUtility.getNotificationDao().deleteInTx(notificationSmsesList);
    }
}

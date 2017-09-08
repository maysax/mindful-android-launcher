package co.siempo.phone.notification.remove_notification_strategy;

import java.util.List;

import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.notification.Notification;

/**
 * Created by tkb on 2017-04-03.
 */


public class MultipleIteamDelete implements DeleteStrategy {
    @Override
    public void delete(Notification notification) {
        try {
            List<TableNotificationSms> notificationSmsesList = DBUtility.getNotificationDao().queryBuilder()
                    .where(TableNotificationSmsDao.Properties._contact_title.eq(notification.getNumber()),
                            TableNotificationSmsDao.Properties.Notification_type.eq(notification.getNotificationType()))
                    .list();

            DBUtility.getNotificationDao().deleteInTx(notificationSmsesList);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}

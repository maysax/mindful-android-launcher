package co.siempo.phone.db;

import java.util.List;

import co.siempo.phone.app.CoreApplication;
import co.siempo.phone.interfaces.DeleteStrategy;
import co.siempo.phone.models.Notification;

/**
 * Created by tkb on 2017-04-03.
 */


public class MultipleItemDelete implements DeleteStrategy {
    @Override
    public void delete(Notification notification) {
        try {
            List<TableNotificationSms> notificationSmsesList = DBUtility.getNotificationDao().queryBuilder()
                    .where(TableNotificationSmsDao.Properties._contact_title.eq(notification.getNumber()),
                            TableNotificationSmsDao.Properties.Notification_type.eq(notification.getNotificationType()))
                    .list();

            DBUtility.getNotificationDao().deleteInTx(notificationSmsesList);
        } catch (Exception e) {
            CoreApplication.getInstance().logException(e);
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAll() {
        DBUtility.getNotificationDao().deleteAll();
    }
}

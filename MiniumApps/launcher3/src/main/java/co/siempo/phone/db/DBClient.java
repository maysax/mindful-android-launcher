package co.siempo.phone.db;

import java.util.List;

import minium.co.core.log.Tracer;


public class DBClient {

    public void deleteMsgByType(int type) {
        Tracer.d("Deleting Msg by type");
        List<TableNotificationSms> notificationSmsesList = DBUtility.getNotificationDao().queryBuilder()
                .where(TableNotificationSmsDao.Properties.Notification_type.eq(type))
                .list();

        DBUtility.getNotificationDao().deleteInTx(notificationSmsesList);
    }
}

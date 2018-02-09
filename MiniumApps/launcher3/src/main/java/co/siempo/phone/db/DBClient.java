package co.siempo.phone.db;

import java.util.List;

import co.siempo.phone.log.Tracer;


public class DBClient {

    public void deleteMsgByType(int type) {
        Tracer.d("Deleting Msg by type");
        List<TableNotificationSms> notificationSmsesList = DBUtility.getNotificationDao().queryBuilder()
                .where(TableNotificationSmsDao.Properties.Notification_type.eq(type))
                .list();

        DBUtility.getNotificationDao().deleteInTx(notificationSmsesList);
    }

    public void deleteMsgById(int type) {
        Tracer.d("Deleting Msg by type");
        List<TableNotificationSms> notificationSmsesList = DBUtility.getNotificationDao().queryBuilder()
                .where(TableNotificationSmsDao.Properties.Id.eq(type))
                .list();

        DBUtility.getNotificationDao().deleteInTx(notificationSmsesList);
    }

    public void deleteMsgByPackageName(String packageName) {
        Tracer.d("Deleting Msg by PackageName");
        List<TableNotificationSms> tableNotificationSms = DBUtility.getNotificationDao().queryBuilder()
                .where(TableNotificationSmsDao.Properties.PackageName.eq(packageName)).list();
        DBUtility.getNotificationDao().deleteInTx(tableNotificationSms);

        DBUtility.getNotificationDao().deleteInTx(tableNotificationSms);
    }
}

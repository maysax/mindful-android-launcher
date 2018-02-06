package co.siempo.phone.db;

import co.siempo.phone.app.Launcher3App_;


public class DBUtility {
    private static TableNotificationSmsDao notificationDao = null;

    private DBUtility() {

    }

    public static TableNotificationSmsDao getNotificationDao() {
        if (notificationDao == null) {
            notificationDao = Launcher3App_.getInstance().getDaoSession().getTableNotificationSmsDao();
        }
        return notificationDao;

    }


}

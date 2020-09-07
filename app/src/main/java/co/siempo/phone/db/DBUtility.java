package co.siempo.phone.db;

import co.siempo.phone.app.Launcher3App;

public class DBUtility {
    private static TableNotificationSmsDao notificationDao = null;

    public static TableNotificationSmsDao getNotificationDao() {
        if (notificationDao == null) {
            notificationDao = Launcher3App.getInstance().getDaoSession().getTableNotificationSmsDao();
        }
        return notificationDao;
    }
}

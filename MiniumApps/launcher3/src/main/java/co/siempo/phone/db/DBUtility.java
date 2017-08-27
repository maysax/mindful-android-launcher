package co.siempo.phone.db;

import co.siempo.phone.app.Launcher3App_;


public class DBUtility {
    private DBUtility() {

    }

    private static ActivitiesStorageDao daoSession = null;

    // Mindful morning activities
    public static ActivitiesStorageDao getActivitySession() {
        if (daoSession == null) {
            daoSession = Launcher3App_.getInstance().getDaoSession().getActivitiesStorageDao();

        }
        return daoSession;
    }


    private static CallStorageDao callStorageDao = null;

    //
    public static CallStorageDao getCallStorageDao() {
        if (callStorageDao == null) {
            callStorageDao = Launcher3App_.getInstance().getDaoSession().getCallStorageDao();

        }
        return callStorageDao;
    }

    private static DaysOfWeekWhichWasSetAlarmDao weekOfDays = null;

    // Mindful morning weekly
    public static DaysOfWeekWhichWasSetAlarmDao getAlarmDaysDao() {
        if (weekOfDays == null) {
            weekOfDays = Launcher3App_.getInstance().getDaoSession().getDaysOfWeekWhichWasSetAlarmDao();
        }
        return weekOfDays;

    }

    private static TableNotificationSmsDao notificationDao = null;

    public static TableNotificationSmsDao getNotificationDao() {
        if (notificationDao == null) {
            notificationDao = Launcher3App_.getInstance().getDaoSession().getTableNotificationSmsDao();
        }
        return notificationDao;

    }

    private static StatusBarNotificationStorageDao statusStorageDao = null;

    public static StatusBarNotificationStorageDao getStatusStorageDao() {
        if (statusStorageDao == null) {
            statusStorageDao = Launcher3App_.getInstance().getDaoSession().getStatusBarNotificationStorageDao();

        }
        return statusStorageDao;

    }

    private static TableNotificationSmsDao tableNotificationSmsDao = null;

    public static TableNotificationSmsDao getTableNotificationSmsDao() {
        if (tableNotificationSmsDao == null) {
            tableNotificationSmsDao = Launcher3App_.getInstance().getDaoSession().getTableNotificationSmsDao();
        }
        return tableNotificationSmsDao;
    }

}

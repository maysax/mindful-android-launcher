package co.siempo.phone.applist;

import android.content.Context;

import java.util.List;

import co.siempo.phone.app.Constants;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.event.TopBarUpdateEvent;
import de.greenrobot.event.EventBus;

/**
 * Created by Shahab on 5/11/2017.
 */

public class AppOpenHandler {

    public void handle(Context context, AppOpenEvent event) {
        if (AppUtil.isDefaultSmsApp(context, event.getPackageName())) {
            List<TableNotificationSms> notificationSmsesList = DBUtility.getNotificationDao().queryBuilder()
                    .where(TableNotificationSmsDao.Properties.Notification_type.eq(0))
                    .list();
            DBUtility.getNotificationDao().deleteInTx(notificationSmsesList);

            EventBus.getDefault().post(new TopBarUpdateEvent());

        } else if (event.getPackageName().equals(Constants.CALL_APP_PACKAGE)) {
            List<TableNotificationSms> notificationSmsesList = DBUtility.getNotificationDao().queryBuilder()
                    .where(TableNotificationSmsDao.Properties.Notification_type.eq(1))
                    .list();
            DBUtility.getNotificationDao().deleteInTx(notificationSmsesList);

            EventBus.getDefault().post(new TopBarUpdateEvent());

        }
    }
}

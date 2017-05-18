package co.siempo.phone.applist;

import android.content.Context;

import java.util.List;

import co.siempo.phone.app.Constants;
import co.siempo.phone.db.DBClient;
import co.siempo.phone.db.DBUtility;
import co.siempo.phone.db.TableNotificationSms;
import co.siempo.phone.db.TableNotificationSmsDao;
import co.siempo.phone.event.TopBarUpdateEvent;
import co.siempo.phone.notification.NotificationUtility;
import de.greenrobot.event.EventBus;

/**
 * Created by Shahab on 5/11/2017.
 */

public class AppOpenHandler {

    public void handle(Context context, AppOpenEvent event) {
        if (AppUtil.isDefaultSmsApp(context, event.getPackageName())) {
            new DBClient().deleteMsgByType(NotificationUtility.NOTIFICATION_TYPE_SMS);
            EventBus.getDefault().post(new TopBarUpdateEvent());

        } else if (event.getPackageName().equals(Constants.CALL_APP_PACKAGE)) {
            new DBClient().deleteMsgByType(NotificationUtility.NOTIFICATION_TYPE_CALL);
            EventBus.getDefault().post(new TopBarUpdateEvent());

        }
    }
}
